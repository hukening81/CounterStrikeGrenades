package club.pisquad.minecraft.csgrenades.entity.core.trajectory

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.POSITION_ERROR_TOLERANCE
import club.pisquad.minecraft.csgrenades.VELOCITY_ERROR_TOLERANCE
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.time.Clock
import kotlin.time.Instant

class Trajectory(
    val bounceCB: Function2<Vec3, Direction, Unit>,
    val hitEntityCB: Function3<Vec3, Direction, Entity, Unit>,
) {
    var beginTime: Instant = Clock.System.now()
    var initialized: Boolean = false
    var completed: Boolean = false
    val currentTick: Int
        get() {
            return nodes.last().tick
        }
    private var _tickCounter: Int = -1
    private val tickCounter: Int
        get() {
            _tickCounter += 1
            return _tickCounter
        }

    val nodes: MutableList<TrajectoryNode.TickNode> = mutableListOf()

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
        this.nodes.add(TrajectoryNode.TickNode(0, position, velocity))
    }

    fun addNode(node: TrajectoryNode.TickNode): Boolean {
        return if (this.nodes.findLast { it.tick == node.tick } != null) {
            CounterStrikeGrenades.Logger.warn("Trajectory node already exits")
            false
        } else {
            this.nodes.add(node)
            true
        }
    }


    fun getNode(index: Int): TrajectoryNode.TickNode? {
        return nodes.getOrNull(index)
    }

//    fun nodesBetweenTick(begin: Double, end: Double): List<TrajectoryNode> {
//        val result = nodes.filter { it.tick in begin..end }
//        return result.sortedBy { it.tick }
//    }

    fun tick(level: Level): TrajectoryNode.TickNode {
        if (!this.initialized) {
            throw Exception("Use before initialization")
        }
        this.nodes.add(this.nodes.last().processTick(level, this.bounceCB, this.hitEntityCB))
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
     * should only be call on client side
     * */
    fun syncServerNode(node: TrajectoryNode.TickNode, level: ClientLevel): Int {
        // Server is ahead
        val clientNode = this.getNode(node.tick)
        if (clientNode == null) {
            var counter = 1
            while (currentTick <= node.tick - 1) {
                counter++
                this.tick(level)
            }
            this.addNode(node)
            return counter
        } else if (
            clientNode.position.distanceTo(node.position) > POSITION_ERROR_TOLERANCE
            || clientNode.velocity.distanceTo(node.velocity) > VELOCITY_ERROR_TOLERANCE
        ) {
            var lastNode = node
            var counter = 0
            while (lastNode.tick == currentTick) {
                this.nodes[lastNode.tick] = lastNode
                lastNode = lastNode.processTick(level, this.bounceCB, this.hitEntityCB)
                counter++
            }
            this.nodes[lastNode.tick] = lastNode
            return counter
        } else {
            return 0
        }
    }

//    @Serializable
//    class TrajectoryNode(
//        @Serializable(with = Vec3Serializer::class) val position: Vec3,
//        @Serializable(with = Vec3Serializer::class) val velocity: Vec3,
//        val tick: Double,
//    ) {
//       companion object{
//           fun empty(): TrajectoryNode {
//               return TrajectoryNode(Vec3.ZERO, Vec3.ZERO, 0.0)
//           }
//       }
//    }

}
