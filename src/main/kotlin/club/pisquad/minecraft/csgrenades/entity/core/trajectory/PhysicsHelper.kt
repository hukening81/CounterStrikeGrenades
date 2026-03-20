package club.pisquad.minecraft.csgrenades.entity.core.trajectory

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.math.Segment
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3

object PhysicsHelper {

    /**Apply air drag and gravity to velocity
     * @param velocity Original velocity
     * @param partialTick between 0 and 1,
     * @return new velocity*/
    fun applyVelocityPhysics(velocity: Vec3, partialTick: Double): Vec3 {
        val x = Mth.lerp(partialTick, velocity.x, velocity.x.times(AIR_DRAG_CONSTANT))
        val y = Mth.lerp(partialTick, velocity.y, velocity.y.times(AIR_DRAG_CONSTANT))
        val z = Mth.lerp(partialTick, velocity.z, velocity.z.times(AIR_DRAG_CONSTANT))

        val v = Vec3(x, y, z).add(0.0, -GRAVITY_CONSTANT.times(partialTick), 0.0)
//        if (v.lengthSqr() > 100) {
//            ModLogger.warn("New velocity is more than 10 blocks per tick")
//        }
        return v
    }

    fun getVelocityAfterBounce(position: Vec3, point: Vec3, velocity: Vec3, direction: Direction): Vec3 {
        val partialTick = position.distanceTo(point).div(velocity.length())
        val velocityAtBouncePoint = applyVelocityPhysics(velocity, partialTick)
        val v = when (direction.axis) {
            Direction.Axis.X -> {
                Vec3(
                    -velocityAtBouncePoint.x.times(BOUNCE_RESTORATION_RATE),
                    velocityAtBouncePoint.y.times(1 - BOUNCE_FRICTION),
                    velocityAtBouncePoint.z.times(1 - BOUNCE_FRICTION)
                )
            }

            Direction.Axis.Y -> {
                Vec3(
                    velocityAtBouncePoint.x.times(1 - BOUNCE_FRICTION),
                    -velocityAtBouncePoint.y.times(BOUNCE_RESTORATION_RATE),
                    velocityAtBouncePoint.z.times(1 - BOUNCE_FRICTION)
                )
            }

            Direction.Axis.Z -> {
                Vec3(
                    velocityAtBouncePoint.x.times(1 - BOUNCE_FRICTION),
                    velocityAtBouncePoint.y.times(1 - BOUNCE_FRICTION),
                    -velocityAtBouncePoint.z.times(BOUNCE_RESTORATION_RATE)
                )
            }
        }
        return if (v.length() < MINIMUM_VELOCITY_AFTER_BOUNCE) {
            Vec3.ZERO
        } else {
            v
        }
    }

    fun getBlocksInPath(displacement: Segment): List<BlockPos> {
        val segments: MutableList<Segment> = mutableListOf()
        displacement.divideAlongAxis(Direction.Axis.X)
            .map { i -> i.divideAlongAxis(Direction.Axis.Y) }
            .forEach { j ->
                j.forEach {
                    segments.addAll(it.divideAlongAxis(Direction.Axis.Z))
                }
            }

        // We will sort it just in case
        return segments.map { it.midPoint() }.sortedBy { it.distanceToSqr(displacement.begin) }
            .map { BlockPos.containing(it) }
    }


}
