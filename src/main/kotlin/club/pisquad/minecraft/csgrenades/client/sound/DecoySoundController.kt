package club.pisquad.minecraft.csgrenades.client.sound

import club.pisquad.minecraft.csgrenades.compat.tacz.TaczApiHandler
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraftforge.fml.ModList
import kotlin.random.Random

object DecoySoundController {

    fun playShotSound(entityId: Int, gunId: String, customSound: String? = null) {
        val mc = net.minecraft.client.Minecraft.getInstance()
        val level = mc.level ?: return
        val entity = level.getEntity(entityId) ?: return

        if (customSound != null && customSound.isNotBlank()) {
            playCustomSound(entity, customSound)
        } else if (ModList.get().isLoaded("tacz") && gunId.isNotBlank()) {
            TaczApiHandler.playGunSound(entity, ResourceLocation(gunId))
        } else {
            playFallbackSound(entity)
        }
    }

    private fun playCustomSound(entity: Entity, soundId: String) {
        val soundEvent = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(ResourceLocation(soundId))
        if (soundEvent != null) {
            entity.level().playSound(null, entity.blockPosition(), soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f)
        } else {
            playFallbackSound(entity)
        }
    }

    private fun playFallbackSound(entity: Entity) {
        val fallbackSounds = arrayOf(
            SoundEvents.CREEPER_HURT,
            SoundEvents.CREEPER_DEATH,
            SoundEvents.CREEPER_PRIMED,
            SoundEvents.CHICKEN_HURT,
            SoundEvents.CHICKEN_AMBIENT,
        )
        val randomSound = fallbackSounds[Random.nextInt(fallbackSounds.size)]
        entity.level().playSound(null, entity.blockPosition(), randomSound, SoundSource.PLAYERS, 1.0f, 1.0f)
    }
}
