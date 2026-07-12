package club.pisquad.minecraft.csgrenades.core.entity.trajectory

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.ModSettings.Entity.SERVER_TRAJECTORY_NODE_CACHE_SIZE
import club.pisquad.minecraft.csgrenades.network.message.ServerGrenadeMovementSyncMessage
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class Trajectory(
    val hitBlockCB: Function1<SubtickNode.BlockBounceData, Unit>,
    val hitEntityCB: Function1<SubtickNode.EntityBounceData, Unit>,
    val completeCB: Function0<Unit>,
    val errorCB:Function1<TrajectoryError,Unit>
) {
    companion object {
        const val TRAJECTORY_MAX_TICK = 20 * 15;
    }

    var beginTime: Instant = Clock.System.now()
    var initialized: Boolean = false
    val serverNodeCaches: ServerNodeCache = ServerNodeCache()

    val completed: Boolean
        get() {
            return nodes.last().completed
        }
    val currentTick: Int
        get() {
            return nodes.last().tick
        }

    val nodes: MutableList<TickNode> = mutableListOf()

    val position: Vec3
        get() {
            return nodes.last().position
        }
    val velocity: Vec3
        get() {
            return nodes.last().velocity
        }

    init {
        beginTime = Clock.System.now()
    }

    fun initialize(position: Vec3, velocity: Vec3) {
        initialized = true
        this.nodes.add(TickNode(0, position, velocity))
    }

    fun tick(level: Level, invokeCB: Boolean = true): TickNode {
        if (!this.initialized) {
            throw Exception("Used before initialization")
        }
        if (this.completed) {
            return this.nodes.last()
        }

        this.nodes.add(this.nodes.last().processTick(level))

        if (this.currentTick>=TRAJECTORY_MAX_TICK){
            this.nodes.last().completed = true
            errorCB(TrajectoryError.MaxTrajectoryTickReached)
            return this.nodes.last()
        }

        if (invokeCB) {
            this.nodes.last().subtickNodes.forEach {
                val data = it.bounceData
                if (data is SubtickNode.BlockBounceData) {
                    this.hitBlockCB(it.bounceData)
                } else if (data is SubtickNode.EntityBounceData) {
                    this.hitEntityCB(it.bounceData)
                }
            }

            if (nodes.last().completed) {
                this.completeCB()
            }
        }

        return this.nodes.last()
    }

    // Minecraft can't hanlde this!
    fun tickUntilComplete(level: Level): Int {
        while (!this.completed) {
            this.tick(level)
        }
        return this.currentTick
    }

    fun createSyncMessage(entity: Entity): ServerGrenadeMovementSyncMessage{
        return ServerGrenadeMovementSyncMessage(
            entity.id,
            this.currentTick,
            this.completed,
            this.position,
            this.velocity
        )
    }

    /**Replace specific node with server's node and update nodes since
     * should only be called on client side
     *
     * NOTE: on a single player setting, server is always ahead by one node, we have to compensate this
     * by allowing the client to be behind a few node
     * */
    fun sync(msg: ServerGrenadeMovementSyncMessage) {
        ModLogger.trace("Syncing server node: tick(${msg.tick}) id(${msg.entityId})")

        // Cache future nodes
        val serverNode = TickNode.fromSyncMessage(msg)
        val clientNode = this.nodes.find { it.tick ==msg.tick }
        if (clientNode == null) {
            ModLogger.trace("Tick(${msg.tick}) not found for ${msg.entityId}, putting it in cache")
            serverNodeCaches.add(serverNode)
        } else if (clientNode.compareServerNode(serverNode)) {
            // Do error correction
            // This will not invoke any callbacks
            val count = this.nodes.last().tick - serverNode.tick

            ModLogger.trace("Tick(${serverNode.tick} error for ${msg.entityId} is too big, correcting $count nodes")

            this.nodes.dropLast(count)

            //TODO(hukenign81): Replace this with a safer approach
            val level: Level = Minecraft.getInstance().player!!.level()
            repeat(count) {
                this.tick(level, false)
            }
        }
    }
}

sealed interface TrajectoryError{
    object MaxTrajectoryTickReached: TrajectoryError{
        fun getTrajectoryMaxTick(): Number{
            return Trajectory.TRAJECTORY_MAX_TICK
        }
    }
}

// Only caches future node
class ServerNodeCache {
    // add from back and remove from front
    private val queue: ArrayDeque<TickNode> = ArrayDeque()

    fun add(node: TickNode) {
        while (queue.size > SERVER_TRAJECTORY_NODE_CACHE_SIZE) {
            queue.removeFirst()
        }
        queue.addLast(node)
    }

    fun find(tick: Int): TickNode? {
        return queue.find { it.tick == tick }
    }

    fun lastOrNull(): TickNode? {
        return queue.lastOrNull()
    }
}
