package club.pisquad.minecraft.csgrenades.util.trajectory

import club.pisquad.minecraft.csgrenades.minus
import club.pisquad.minecraft.csgrenades.minusLength
import club.pisquad.minecraft.csgrenades.toVec3
import club.pisquad.minecraft.csgrenades.util.EntityBounceHelper.between
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.math.absoluteValue

object TrajectoryHelper {
    const val MAX_STEP_COUNT = 1000

    fun step(level: Level, trajectory: Trajectory): Boolean {
        var currentPosition = trajectory.lastNode().position
        var currentVelocity = trajectory.lastNode().velocity
        var partialTick: Double = 0.0
        while (partialTick < 1) {
            val (newPos, newVel, newPt) = tryTravelInDirection(level, currentPosition, currentVelocity, partialTick)
            trajectory.addNode(newPos, newVel, newPt)
            currentPosition = newPos
            currentVelocity = newVel
            partialTick = newPt
        }
        trajectory.tickComplete()
        return false
    }

    fun stepUntilComplete(level: Level, trajectory: Trajectory) {
        var counter = 0
        while (counter < MAX_STEP_COUNT) {
            if (step(level, trajectory)) return
            counter++
        }
    }


    private fun tryTravelInDirection(level: Level, position: Vec3, velocity: Vec3, partialTick: Double): Triple<Vec3, Vec3, Double> {
        val obstructingBlocks = getBlockPosInPath(
            position,
            velocity.scale(1 - partialTick),
        )
            .filter { shouldBounceOnBlock(level, it) }
//            .sortedBy { it.center.distanceToSqr(position) }

//
        for (blockPos in obstructingBlocks) {
            val collidingResult = testCollision(level, position, velocity, blockPos) ?: continue
            val partialTickDelta = collidingResult.collidingPoint.distanceTo(position).div(velocity.length())
            val newVelocity = getVelocityAfterBounce(applyPhysics(velocity, partialTickDelta), collidingResult.direction)
//            println("${this.level.isClientSide},bounce! $newVelocity")
//            println("is inside ${BlockPos.containing(position) == blockPos}")

            return Triple(collidingResult.collidingPoint, newVelocity, partialTick + partialTickDelta)

        }
//        println("${this.level.isClientSide}, no bounce")
        return Triple(position.add(velocity.scale(1 - partialTick)), applyPhysics(velocity), 2.0)
    }

    private fun testCollision(level: Level, position: Vec3, velocity: Vec3, blockPos: BlockPos): CollidingResult? {
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
        result.add(BlockPos.containing(begin))
        var v = displacement
        while (v.length() > 1) {
            result.add(BlockPos.containing(begin.add(v)))
            v = v.minusLength(1.0)
        }
        return result.toList().sortedBy { it.center.distanceTo(begin) }
    }

    private fun applyPhysics(velocity: Vec3, partialTick: Double = 1.0): Vec3 {
        // DDD
//        return velocity.minusLength(AIR_DRAG_CONSTANT.times(partialTick)).minus(0.0, GRAVITY_CONSTANT.times(partialTick), 0.0)
        return velocity
    }

    private fun shouldBounceOnBlock(level: Level, blockPos: BlockPos): Boolean = !level.getBlockState(blockPos).isAir


    class CollidingResult(
        val direction: Direction,
        val length: Double,
        val collidingPoint: Vec3,
    )
}
