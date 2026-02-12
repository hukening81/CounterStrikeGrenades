package club.pisquad.minecraft.csgrenades.client.sound

import club.pisquad.minecraft.csgrenades.compat.tacz.TaczApiHandler
import club.pisquad.minecraft.csgrenades.entity.DecoyGrenadeEntity
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraftforge.fml.ModList
import kotlin.random.Random

object DecoySoundController {

    private val lastSoundCounters = mutableMapOf<Int, Int>()

    fun onClientTick() {
        val mc = Minecraft.getInstance()
        val level = mc.level ?: return
        val player = mc.player ?: return

        // 只在玩家周围大半径内放置诱饵实体以提高效率
        val decoys = level.getEntitiesOfClass(DecoyGrenadeEntity::class.java, player.boundingBox.inflate(256.0))
        decoys.forEach { decoy ->
            processDecoy(decoy)
        }
    }

    private fun processDecoy(entity: DecoyGrenadeEntity) {
        val entityId = entity.id
        val lastCounter = lastSoundCounters.getOrDefault(entityId, 0)
        val currentCounter = entity.entityData.get(DecoyGrenadeEntity.SOUND_COUNTER_ACCESSOR)

        if (currentCounter > lastCounter) {
            val gunId = entity.entityData.get(DecoyGrenadeEntity.GUN_ID_TO_PLAY_ACCESSOR)

            if (ModList.get().isLoaded("tacz") && gunId.isNotBlank()) {
                TaczApiHandler.playGunSound(entity, ResourceLocation(gunId))
            } else {
                playFallbackSound(entity)
            }
            lastSoundCounters[entityId] = currentCounter
        }

        if (entity.isRemoved) {
            lastSoundCounters.remove(entityId)
        }
    }

    private fun playFallbackSound(entity: DecoyGrenadeEntity) {
        val footstepSounds = arrayOf(
            SoundEvents.CREEPER_HURT,
            SoundEvents.CREEPER_DEATH,
            SoundEvents.CREEPER_PRIMED,
            SoundEvents.CHICKEN_HURT,
            SoundEvents.CHICKEN_AMBIENT,
        )
        val randomSoundHolder = footstepSounds[Random.nextInt(footstepSounds.size)]
        entity.level().playSound(null, entity.blockPosition(), randomSoundHolder, SoundSource.PLAYERS, 1.0f, 1.0f)
    }

    fun onClientStop() {
        lastSoundCounters.clear()
    }
}
