package club.pisquad.minecraft.csgrenades.grenades.hegrenade.client

import club.pisquad.minecraft.csgrenades.getRandomLocationFromSphere
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.phys.Vec3
import kotlin.random.Random

object HEGrenadeExplosionRenderer {
    var done: Boolean = false
    private var soundInstance: SoundInstance? = null

    fun renderSingle(position: Vec3) {
        //Maybe we should define a custom particle type and use level.addParticle
        val particleEngine = Minecraft.getInstance().particleEngine

        for (i in 1..500) {
            particleEngine.createParticle(
                ParticleTypes.SMOKE,
                position.x,
                position.y,
                position.z,
                Random.nextDouble().times(1.4) - 0.7,
                Random.nextDouble().times(1.4) - 0.7,
                Random.nextDouble().times(1.4) - 0.7,
            )?.lifetime = 10
        }
        for (i in 1..100) {
            val location = getRandomLocationFromSphere(position, 4.0)
            particleEngine.createParticle(
                ParticleTypes.LARGE_SMOKE,
                location.x,
                location.y,
                location.z,
                0.0,
                0.0,
                0.0,
            )?.scale(1.5f)?.lifetime = 20
        }
    }

    fun update(): Boolean = true
}
