package club.pisquad.minecraft.csgrenades.util

import club.pisquad.minecraft.csgrenades.GRENADE_ENTITY_SIZE_HALF
import club.pisquad.minecraft.csgrenades.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.minus
import club.pisquad.minecraft.csgrenades.toVec3
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.VoxelShape
import kotlin.math.absoluteValue

/**
 * We use grenade center's path when detecting collision
 * this may not be accurately represented by the visual model
 * */
object EntityBounceHelper {
    fun bounce(level: Level, direction: Direction, blockPos: BlockPos, grenade: CounterStrikeGrenadeEntity): BounceResult {
        val velocity = grenade.deltaMovement
        val blockState = level.getBlockState(blockPos)

//        val relativePos = if (result.isInside) {
//            grenade.center.minus(blockPos.toVec3()).add(velocity.reverse().normalize())
//        } else {
//            grenade.center.minus(blockPos.toVec3())
//        }
        val relativePos = grenade.center.minus(blockPos.toVec3())

//        repeat(10) {
//            val bounceResult = bounceOnce(grenade.level(), blockState, blockPos, position, velocity) ?: return BounceResult(position.add(blockPos.center), velocity)
//            position = bounceResult.position
//            velocity = bounceResult.velocity
//            println("COUNTER")
//        }
        // Get Point of collision
        val shape = blockState.getCollisionShape(grenade.level(), blockPos)
        val pointOfCollision = getCollisionPoint(relativePos, velocity, shape, direction)
        val distance = relativePos.distanceTo(pointOfCollision)
        val newVelocity = getSpeedAfterBounce(velocity, direction)
//        val newPosition = if (result.isInside) {
//            pointOfCollision.add(newVelocity.normalize().scale(0.1))
//        } else {
//            pointOfCollision.add(newVelocity.scale(1 - distance.div(velocity.length())))
//        }
        val newPosition = pointOfCollision.add(newVelocity.scale(1 - distance.div(velocity.length())))

        return BounceResult(newPosition.add(blockPos.toVec3().add(0.0, GRENADE_ENTITY_SIZE_HALF, 0.0)), newVelocity)
    }

    private fun getCollisionPoint(position: Vec3, velocity: Vec3, shape: VoxelShape, direction: Direction): Vec3 {
        when (direction) {
            Direction.DOWN -> {
                val y = shape.min(Direction.Axis.Y)
                val d = y - position.y
                val c = d.div(velocity.y.absoluteValue)
                val v = velocity.scale(c)
                return position.add(v)
            }

            Direction.UP -> {
                val y = shape.max(Direction.Axis.Y)
                val d = position.y - y
                val c = d.div(velocity.y.absoluteValue)
                val v = velocity.scale(c)
                return position.add(v)
            }

            Direction.NORTH -> {
                val z = shape.min(Direction.Axis.Z)
                val d = position.z - z
                val c = d.div(velocity.z.absoluteValue)
                val v = velocity.scale(c)
                return position.add(v)
            }

            Direction.SOUTH -> {
                val z = shape.max(Direction.Axis.Z)
                val d = z - position.z
                val c = d.div(velocity.z.absoluteValue)
                val v = velocity.scale(c)
                return position.add(v)
            }

            Direction.WEST -> {
                val x = shape.min(Direction.Axis.X)
                val d = x - position.x
                val c = d.div(velocity.x.absoluteValue)
                val v = velocity.scale(c)
                return position.add(v)
            }

            Direction.EAST -> {
                val x = shape.max(Direction.Axis.X)
                val d = position.x - x
                val c = d.div(velocity.x.absoluteValue)
                val v = velocity.scale(c)
                return position.add(v)
            }
        }
    }

//    private fun bounceOnce(level: Level, blockState: BlockState, blockPos: BlockPos, position: Vec3, velocity: Vec3): BounceResult? {
//        val collidingResult = findFirstColliding(level, blockState, position, velocity, blockPos) ?: return null
//        return BounceResult(collidingResult.collidingPoint, this.getSpeedAfterBounce(velocity.minusLength(collidingResult.length), collidingResult.direction))
//    }
//
//    private fun findFirstColliding(level: Level, blockState: BlockState, position: Vec3, velocity: Vec3, blockPos: BlockPos): CollidingResult? {
//        val velocity = velocity
//        val shapes = blockState.getCollisionShape(level, blockPos).toAabbs()
//        val collisions = shapes.mapNotNull { testCollision(it, velocity, position) }.sortedBy { it.length }
//
//        return if (collisions.isEmpty()) {
//            null
//        } else {
//            collisions[0]
//        }
//    }

    private fun getSpeedAfterBounce(velocity: Vec3, direction: Direction): Vec3 {
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
//        return Vec3(newVelocity.x, newVelocity.y - 0.1, newVelocity.z)
        return newVelocity
    }

    fun Double.between(a: Double, b: Double): Boolean = if (a > b) {
        this > b && this < a
    } else {
        this > a && this < b
    }

    class BounceResult(val position: Vec3, val velocity: Vec3)
}
