package club.pisquad.minecraft.csgrenades.physics

import club.pisquad.minecraft.csgrenades.addGrenadeSizeOffset
import club.pisquad.minecraft.csgrenades.minusGrenadeSizeOffset
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

@Serializable
class GrenadePosition private constructor(@Serializable(with = Vec3Serializer::class) val center: Vec3) {
    companion object {
        val ZERO = GrenadePosition.fromCenter(Vec3.ZERO)

        fun fromWorldPos(position: Vec3): GrenadePosition {
            return GrenadePosition(position.addGrenadeSizeOffset())
        }

        fun fromCenter(center: Vec3): GrenadePosition {
            return GrenadePosition(center)
        }
    }

    val worldPos: Vec3
        get() {
            return this.center.minusGrenadeSizeOffset()
        }

    fun add(other: GrenadePosition): GrenadePosition {
        return GrenadePosition(this.center.add(other.center))
    }

    fun move(velocity: GrenadeVelocity, duration: GrenadeDuration): GrenadePosition {
        return GrenadePosition(this.center.add(velocity.metersPerSecond.scale(duration.seconds)))
    }

    fun levelWith(other: GrenadePosition): GrenadePosition {
        return GrenadePosition(
            Vec3(
                this.center.x,
                other.center.y,
                this.center.z,
            )
        )

    }
}

@Serializable
class GrenadeVelocity private constructor(@Serializable(with = Vec3Serializer::class) val metersPerSecond: Vec3) {
    companion object {
        val ZERO = GrenadeVelocity.fromMetersPerSecond(Vec3.ZERO)

        fun fromBlocksPerTick(v: Vec3): GrenadeVelocity {
            return GrenadeVelocity(v.scale(20.0))
        }

        fun fromMetersPerSecond(v: Vec3): GrenadeVelocity {
            return GrenadeVelocity(v)
        }

        fun lerp(factor: Double, start: GrenadeVelocity, end: GrenadeVelocity): GrenadeVelocity {
            return GrenadeVelocity(
                Vec3(
                    Mth.lerp(factor, start.metersPerSecond.x, end.metersPerSecond.x),
                    Mth.lerp(factor, start.metersPerSecond.y, end.metersPerSecond.y),
                    Mth.lerp(factor, start.metersPerSecond.z, end.metersPerSecond.z),
                )
            )
        }

    }

    val blocksPerTick: Vec3
        get() {
            return this.metersPerSecond.scale(1.0 / 20.0)
        }

    fun scale(factor: Double): GrenadeVelocity {
        return GrenadeVelocity(this.metersPerSecond.scale(factor))
    }

    fun add(other: GrenadeVelocity): GrenadeVelocity {
        return GrenadeVelocity(this.metersPerSecond.add(other.metersPerSecond))
    }

    fun capMetersPerSecond(maxSpeed: Double): GrenadeVelocity {
        return if (this.metersPerSecond.length() < maxSpeed) {
            this
        } else {
            this.scale(maxSpeed / this.metersPerSecond.length())
        }
    }
}

@Serializable
class GrenadeDuration private constructor(val seconds: Double) {
    companion object {
        fun fromSeconds(seconds: Double): GrenadeDuration {
            return GrenadeDuration(seconds)
        }

        fun fromTick(tick: Double): GrenadeDuration {
            return GrenadeDuration(tick.div(20))
        }
    }

    val ticks: Double
        get() {
            return seconds.times(20)
        }
}

class GrenadeHitEntity(
    val entity: Entity,
    val hitPoint: Vec3,
    val direction: Direction,
    val velocity: GrenadeVelocity
)

class GrenadeHitBlock(
    val blockPos: BlockPos,
    val hitPoint: Vec3,
    val direction: Direction,
    val velocity: GrenadeVelocity,
)