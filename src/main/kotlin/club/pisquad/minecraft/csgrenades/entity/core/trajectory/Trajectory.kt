package club.pisquad.minecraft.csgrenades.entity.core.trajectory

import club.pisquad.minecraft.csgrenades.SERVER_NODE_CACHE_MAX_SIZE
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class Trajectory(
    val bounceCB: Function2<Vec3, Direction, Unit>,
    val hitEntityCB: Function3<Vec3, Direction, Entity, Unit>,
    val completeCB: Function0<Unit>,
) {
    var beginTime: Instant = Clock.System.now()
    var initialized: Boolean = false
    private val serverNodeCache: ServerNodeCache = ServerNodeCache()
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

    fun tick(level: Level): TickNode {
        if (!this.initialized) {
            throw Exception("Use before initialization")
        }
        if (this.completed) {
            return this.nodes.last()
        }


        this.nodes.add(this.nodes.last().processTick(level, this.bounceCB, this.hitEntityCB))

        if (nodes.last().completed) {
            this.completeCB()
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
        serverNodeCache.add(node)
    }

    private class ServerNodeCache {
        // add from back and remove from front
        private val queue: ArrayDeque<TickNode> = ArrayDeque()

        fun add(node: TickNode) {
            while (queue.size > SERVER_NODE_CACHE_MAX_SIZE) {
                queue.removeFirst()
            }
            queue.addLast(node)
        }

        fun getOrNull(tick: Int): TickNode? {
            return queue.find { it.tick == tick }
        }

        fun getLast(): TickNode? {
            return queue.last()
        }
    }

}
