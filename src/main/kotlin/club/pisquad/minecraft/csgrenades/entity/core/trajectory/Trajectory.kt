package club.pisquad.minecraft.csgrenades.entity.core.trajectory

import net.minecraft.world.phys.Vec3
import kotlin.time.Clock
import kotlin.time.Instant

class Trajectory(position: Vec3, velocity: Vec3) {
    var beginTime: Instant = Clock.System.now()
    var currentTick: Int = 0
    var completed: Boolean = false
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
        addNode(position, velocity, 0.0)
    }

    fun tickComplete() {
        currentTick++
    }

    fun addNode(position: Vec3, velocity: Vec3, partialTick: Double): Trajectory {
        this.nodes.add(TrajectoryNode(position, velocity, currentTick.toDouble() + partialTick))
        return this
    }

    fun lastNode(): TrajectoryNode {
        // provided that there is always one node available
        val size = nodes.size
        if (size > 1) {
            return nodes[size - 2]
        }
        return nodes.last()
    }

    fun getNode(index: Int): TrajectoryNode {
        return nodes[index]
    }

    fun replaceNode(index: Int, node: TrajectoryNode) {
        nodes[index] = node
    }

    fun nodesBetweenTick(begin: Double, end: Double): List<TrajectoryNode> {
        val result = nodes.filter { it.tick in begin..end }
        return result.sortedBy { it.tick }
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
