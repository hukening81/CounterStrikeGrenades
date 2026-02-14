package club.pisquad.minecraft.csgrenades.entity.firegrenade

import club.pisquad.minecraft.csgrenades.registry.*
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance
import net.minecraft.sounds.SoundSource
import kotlin.random.Random

object FireGrenadeHelper {
    fun playExtinguishSound(grenade: AbstractFireGrenadeEntity) {
        val extinguishSoundInstance = EntityBoundSoundInstance(
            ModSoundEvents.INCENDIARY_POP.get(),
            SoundSource.AMBIENT,
            0.5f,
            0.8f,
            grenade,
            Random.nextLong(),
        )
        Minecraft.getInstance().soundManager.play(extinguishSoundInstance)
    }

    fun playFireSound(grenade: AbstractFireGrenadeEntity) {
    }
}
