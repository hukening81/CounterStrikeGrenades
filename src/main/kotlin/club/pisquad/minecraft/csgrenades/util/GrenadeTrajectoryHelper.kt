package club.pisquad.minecraft.csgrenades.util

import club.pisquad.minecraft.csgrenades.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.minus
import club.pisquad.minecraft.csgrenades.minusLength
import club.pisquad.minecraft.csgrenades.toVec3
import club.pisquad.minecraft.csgrenades.util.EntityBounceHelper.between
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.math.absoluteValue
import kotlin.time.Clock
import kotlin.time.Instant

class GrenadeTrajectoryHelper(val level: Level, val grenade: CounterStrikeGrenadeEntity) {
    val trajectory: Trajectory = Trajectory()

    val position: Vec3
        get() {
            return trajectory.last().position
        }
    val velocity: Vec3
        get() {
            return trajectory.last().velocity
        }

    fun init(position: Vec3, velocity: Vec3) {
        trajectory.init(position, velocity)
    }

    fun step() {
        var currentPosition = position
        var currentVelocity = velocity
        var partialTick: Double = 0.0
        while (partialTick < 1) {
            val (newPos, newVel, newPt) = tryTravelInDirection(currentPosition, currentVelocity, partialTick)
            trajectory.addNode(newPos, newVel)
            currentPosition = newPos
            currentVelocity = newVel
            partialTick = newPt
        }
    }

    private fun tryTravelInDirection(position: Vec3, velocity: Vec3, partialTick: Double): Triple<Vec3, Vec3, Double> {
        val obstructingBlocks = getBlockPosInPath(
            position,
            velocity.scale(1 - partialTick),
        )
            .filter { shouldBounceOnBlock(it) }
            .sortedBy { it.center.distanceToSqr(position) }

//
        for (blockPos in obstructingBlocks) {
            val collidingResult = testCollision(position, velocity, blockPos) ?: continue
            val partialTickDelta = collidingResult.collidingPoint.distanceTo(position).div(velocity.length())
            val newVelocity = getVelocityAfterBounce(applyPhysics(velocity, partialTickDelta), collidingResult.direction)
            println(newVelocity)
            return Triple(collidingResult.collidingPoint, newVelocity, partialTick + partialTickDelta)
        }
        return Triple(position.add(velocity.scale(1 - partialTick)), applyPhysics(velocity), 2.0)
    }

    private fun testCollision(position: Vec3, velocity: Vec3, blockPos: BlockPos): CollidingResult? {
        val relativePos = position.minus(blockPos.toVec3())
        val blockState = level.getBlockState(blockPos)
        val box = blockState.getCollisionShape(level, blockPos).bounds()
//        val collisionResult = testCollision(box, position, velocity) ?: return null
        var candidateResult: CollidingResult? = null

        // X axis
        if ((relativePos.x > box.maxX && velocity.x < 0) || (relativePos.x < box.minX && velocity.x > 0)) {
            val d = if (relativePos.x > box.maxX) {
                relativePos.x - box.maxX
            } else {
                box.minX - relativePos.x
            }
            val direction = if (relativePos.x > box.maxX) {
                Direction.EAST
            } else {
                Direction.WEST
            }
            val c = d.div(velocity.x.absoluteValue)
            if (c < 1) {
                val v = velocity.scale(c)
                val l = v.length()
                val p = relativePos.add(v)
                if (p.y.between(box.minY, box.maxY) && p.z.between(box.minZ, box.maxZ)) {
                    candidateResult = CollidingResult(direction, l, p.add(blockPos.toVec3()))
                }
            }
        }
        // Y axis
        if ((relativePos.y > box.maxY && velocity.y < 0) || (relativePos.y < box.minY && velocity.y > 0)) {
            val d = if (relativePos.y > box.maxY) {
                relativePos.y - box.maxY
            } else {
                box.minY - relativePos.y
            }
            val direction = if (relativePos.y > box.maxY) {
                Direction.UP
            } else {
                Direction.DOWN
            }
            val c = d.div(velocity.y.absoluteValue)
            if (c < 1) {
                val v = velocity.scale(c)
                val l = v.length()
                val p = relativePos.add(v)
                if (p.x.between(box.minX, box.maxX) && p.z.between(box.minZ, box.maxZ)) {
                    if (candidateResult == null || l < candidateResult.length) {
                        candidateResult = CollidingResult(direction, l, p.add(blockPos.toVec3()))
                    }
                }
            }
        }
        // Z axis
        if ((relativePos.z > box.maxZ && velocity.z < 0) || (relativePos.z < box.minZ && velocity.z > 0)) {
            val d = if (relativePos.z > box.maxZ) {
                relativePos.z - box.maxZ
            } else {
                box.minZ - relativePos.z
            }
            val direction = if (relativePos.z > box.maxZ) {
                Direction.NORTH
            } else {
                Direction.SOUTH
            }
            val c = d.div(velocity.z.absoluteValue)
            if (c < 1) {
                val v = velocity.scale(c)
                val l = v.length()
                val p = relativePos.add(v)
                if (p.x.between(box.minX, box.maxX) && p.y.between(box.minY, box.maxY)) {
                    if (candidateResult == null || l < candidateResult.length) {
                        candidateResult = CollidingResult(direction, l, p.add(blockPos.toVec3()))
                    }
                }
            }
        }

        return candidateResult
    }

    private fun getVelocityAfterBounce(velocity: Vec3, direction: Direction): Vec3 {
        val c = 0.5
        val f = 0.6
        val newVelocity = when (direction) {
            Direction.DOWN, Direction.UP -> {
                Vec3(velocity.x.times(f), -velocity.y.times(c), velocity.z.times(f))
            }

            Direction.NORTH, Direction.SOUTH -> {
                Vec3(velocity.x.times(f), velocity.y.times(f), -velocity.z.times(c))
            }

            Direction.WEST, Direction.EAST -> {
                Vec3(-velocity.x.times(c), velocity.y.times(f), velocity.z.times(f))
            }
        }
        return newVelocity
    }

    private fun getBlockPosInPath(begin: Vec3, displacement: Vec3): List<BlockPos> {
        val result: MutableSet<BlockPos> = mutableSetOf()
        var v = displacement
        while (v.length() > 1) {
            result.add(BlockPos.containing(begin.add(v)))
            v = v.minusLength(1.0)
        }
        result.add(BlockPos.containing(begin))
        return result.toList().sortedBy { it.center.distanceTo(begin) }
    }

    private fun applyPhysics(velocity: Vec3, partialTick: Double = 1.0): Vec3 {
        // DDD
//        return velocity.minusLength(AIR_DRAG_CONSTANT.times(partialTick)).minus(0.0, GRAVITY_CONSTANT.times(partialTick), 0.0)
        return velocity
    }

    private fun shouldBounceOnBlock(blockPos: BlockPos): Boolean = !level.getBlockState(blockPos).isAir

    class Trajectory {
        var beginTime: Instant = Clock.System.now()
        val nodes: MutableList<TrajectoryNode> = mutableListOf()

        fun init(position: Vec3, velocity: Vec3) {
            beginTime = Clock.System.now()
            addNode(position, velocity)
        }

        fun addNode(position: Vec3, velocity: Vec3, partialTick: Double = 0.0): Trajectory {
            val time = Clock.System.now().minus(beginTime).inWholeMilliseconds.div(1000.0) + partialTick
            this.nodes.add(TrajectoryNode(position, velocity, time))
            return this
        }

        fun last(): TrajectoryNode = nodes.last()
    }

    class TrajectoryNode(
        val position: Vec3,
        val velocity: Vec3,
        val time: Double,
    )

    class CollidingResult(
        val direction: Direction,
        val length: Double,
        val collidingPoint: Vec3,
    )
}
