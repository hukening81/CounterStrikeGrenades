package club.pisquad.minecraft.csgrenades.grenades.hegrenade.client

import club.pisquad.minecraft.csgrenades.grenades.hegrenade.HEGrenadeRegistries
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3
import kotlin.random.Random

object HEGrenadeSoundManager {

    fun playExplosionSound(position: Vec3) {
        val soundManager = Minecraft.getInstance().soundManager
        val instance = SoundInstanceHelper.getExplosionSound(position)
        soundManager.play(instance)
    }
}

private object SoundInstanceHelper {
    fun getExplosionSound(position: Vec3): SimpleSoundInstance {
        return SimpleSoundInstance(
            HEGrenadeRegistries.sounds.explode.getSoundEvent(),
            SoundSource.PLAYERS,
//            HEGrenadeRegistries.sounds.explode.getVolume(),
            0.3f,
            1f,
            RandomSource.create(Random.nextLong()),
            position.x,
            position.y,
            position.z,
        )

    }
}