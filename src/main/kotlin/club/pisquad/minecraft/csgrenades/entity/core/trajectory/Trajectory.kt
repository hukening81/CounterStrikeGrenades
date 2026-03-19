package club.pisquad.minecraft.csgrenades.entity.core.trajectory

import club.pisquad.minecraft.csgrenades.SERVER_TRAJECTORY_NODE_CACHE_MAX_SIZE
import net.minecraft.client.Minecraft
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
) {
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

//    fun nodesBetweenTick(begin: Double, end: Double): List<TrajectoryNode> {
//        val result = nodes.filter { it.tick in begin..end }
//        return result.sortedBy { it.tick }
//    }

    fun tick(level: Level, invokeCB: Boolean = true): TickNode {
        if (!this.initialized) {
            throw Exception("Use before initialization")
        }
        if (this.completed) {
            return this.nodes.last()
        }


        this.nodes.add(this.nodes.last().processTick(level))

        // Do callbacks
        if (invokeCB) {
            this.nodes.last().subtickNodes.forEach {
                if (it.bounceData is SubtickNode.BlockBounceData) {
                    this.hitBlockCB(it.bounceData)
                } else if (it.bounceData is SubtickNode.EntityBounceData) {
                    throw NotImplementedError()
                }
            }

            if (nodes.last().completed) {
                this.completeCB()
            }
        }

        return this.nodes.last()
    }

    // Minecraft can't handle this!!!
    fun tickUntilComplete(level: Level): Int {
        repeat(10) {
            this.tick(level)
        }
        return this.currentTick
    }

    /**Replace specific node with server's node and update nodes since
     * should only be called on client side
     *
     * NOTE: on a single player setting, server is always ahead by one node, we have to compensate this
     * by allowing the client to be behind a few node
     * */
    fun syncServerNode(node: TickNode) {
        // Cache future nodes
        val clientNode = this.nodes.find { it.tick == node.tick }
        if (clientNode == null) {
            serverNodeCaches.add(node)
        } else if (clientNode.compareServerNode(node)) {
            // Do error correction
            // This will not invoke any callbacks
            val count = this.nodes.last().tick - node.tick
            this.nodes.dropLast(count)

            //TODO(hukenign81): Replace this with a safer approach
            val level: Level = Minecraft.getInstance().player!!.level()
            repeat(count) {
                this.tick(level, false)
            }
        }


    }
}

// Only cahces future node
class ServerNodeCache {
    // add from back and remove from front
    private val queue: ArrayDeque<TickNode> = ArrayDeque()

    fun add(node: TickNode) {
        while (queue.size > SERVER_TRAJECTORY_NODE_CACHE_MAX_SIZE) {
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
