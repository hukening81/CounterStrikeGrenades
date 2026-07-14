package club.pisquad.minecraft.csgrenades.physics

import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import kotlin.math.floor

object PhysicsUtil {
    private const val VERTICAL_ACCELERATION: Double = -9.8
//    private const val VERTICAL_ACCELERATION_PER_TICK: Double = VERTICAL_ACCELERATION / 20
    private const val VERTICAL_ACCELERATION_PER_TICK: Double = -0.1
    private const val AIR_DRAG_COEFFICIENT: Double = 0.99
    private const val MINIMUM_VELOCITY_AFTER_BOUNCE = 0.1

    fun updateVelocity(velocity: GrenadeVelocity, duration: GrenadeDuration): GrenadeVelocity {
        val wholeSeconds = floor(duration.seconds).toInt()
        val fraction = duration.seconds - wholeSeconds
        var newVelocity = velocity
        repeat(wholeSeconds) {
            newVelocity = updateVelocityPartialTick(newVelocity, 1.0)
        }
        newVelocity = updateVelocityPartialTick(newVelocity, fraction)
        return newVelocity
    }

    fun updateVelocityPartialTick(velocity: GrenadeVelocity, deltaTick: Double): GrenadeVelocity {
        // air drag
        val v = GrenadeVelocity.lerp(deltaTick, velocity, velocity.scale(AIR_DRAG_COEFFICIENT))

        // gravity
        return GrenadeVelocity.lerp(
            deltaTick,
            v,
            v.add(
                GrenadeVelocity.fromBlocksPerTick(
                    Vec3(
                        0.0,
                        VERTICAL_ACCELERATION_PER_TICK,
                        0.0
                    )
                )
            )
        )
    }

    fun bounceVelocity(velocity: GrenadeVelocity, direction: Direction): GrenadeVelocity {
        val v = velocity.metersPerSecond
//        val newV = GrenadeVelocity.fromMetersPerSecond(v.inverseAxis(direction.axis))

        val newV = when (direction.axis) {
            Direction.Axis.X -> {
                GrenadeVelocity.fromMetersPerSecond(
                    Vec3(
                        -v.x * 0.5, v.y, v.z
                    )
                )
            }

            Direction.Axis.Y -> {
                GrenadeVelocity.fromMetersPerSecond(
                    Vec3(
                        v.x, -v.y * 0.5, v.z
                    )
                )
            }

            Direction.Axis.Z -> {
                GrenadeVelocity.fromMetersPerSecond(
                    Vec3(
                        v.x, v.y, -v.z * 0.5
                    )
                )
            }
        }
        println("Speed before bounce: ${velocity.metersPerSecond.length()}")
        println("Speed after bounce: ${newV.metersPerSecond.length()}")
        return if (newV.metersPerSecond.length() < MINIMUM_VELOCITY_AFTER_BOUNCE) {
            GrenadeVelocity.ZERO
        } else if (direction == Direction.UP && newV.metersPerSecond.y<0.1){
            GrenadeVelocity.ZERO
        }
        else {
            newV
        }
    }

    fun combineWithFall(deltaMovement: Vec3, deltaTick: Double): Vec3 {
//        val d = 0.5 * VERTICAL_ACCELERATION * (SECONDS_PER_TICK * deltaTick).pow(2)
        val d = 0.0
        return Vec3(deltaMovement.x, deltaMovement.y + d, deltaMovement.z)
    }
}