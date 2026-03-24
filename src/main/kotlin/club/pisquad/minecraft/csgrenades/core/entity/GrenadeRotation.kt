package club.pisquad.minecraft.csgrenades.core.entity

import club.pisquad.minecraft.csgrenades.ModSettings.Entity.Physics.ROTATION_SPEED
import net.minecraft.world.phys.Vec3
import org.joml.Quaterniond
import org.joml.Vector3d
import kotlin.random.Random

class GrenadeRotation(
    seed: Long,
) {
    val random = Random(seed)
    var angularVelocity: Vec3 = Vec3.ZERO

    var orientation = Quaterniond()
    var orientationOld = Quaterniond()

    init {
        randomize()
    }

    fun tick() {
        orientationOld.set(orientation)
        orientation.mul(
            Quaterniond().fromAxisAngleRad(
                Vector3d(
                    angularVelocity.x,
                    angularVelocity.y,
                    angularVelocity.z,
                ).normalize(), angularVelocity.length()
            )
        )
    }

    fun getPartialTick(partialTick: Double): Quaterniond {
        return orientationOld.slerp(orientation, partialTick)
    }

    fun randomize() {
        angularVelocity = Vec3(
            random.nextDouble(),
            random.nextDouble(),
            random.nextDouble(),
        )
        angularVelocity.normalize()
        angularVelocity.scale(ROTATION_SPEED)
    }
}