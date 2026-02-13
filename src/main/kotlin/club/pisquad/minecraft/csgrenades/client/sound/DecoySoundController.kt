package club.pisquad.minecraft.csgrenades.client.sound

import club.pisquad.minecraft.csgrenades.compat.tacz.TaczApiHandler
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraftforge.fml.ModList
import kotlin.random.Random

/**
 * 诱饵弹声音控制器
 * 核心功能：处理诱饵弹射击声音的播放
 */
object DecoySoundController {

    /**
     * 播放射击声音
     * 核心机制：根据优先级选择播放自定义声音、TACZ枪械声音或fallback声音
     */
    fun playShotSound(entityId: Int, gunId: String, customSound: String? = null) {
        val mc = net.minecraft.client.Minecraft.getInstance()
        val level = mc.level ?: return
        val entity = level.getEntity(entityId) ?: return

        // 优先播放自定义声音
        if (customSound != null && customSound.isNotBlank()) {
            playCustomSound(entity, customSound)
        } 
        // 其次播放TACZ枪械声音
        else if (ModList.get().isLoaded("tacz") && gunId.isNotBlank()) {
            TaczApiHandler.playGunSound(entity, ResourceLocation(gunId))
        } 
        // 最后使用fallback声音
        else {
            playFallbackSound(entity)
        }
    }

    /**
     * 播放自定义声音
     * 核心逻辑：从资源注册表中获取声音事件并播放
     */
    private fun playCustomSound(entity: Entity, soundId: String) {
        // 从注册表中获取声音事件
        val soundEvent = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(ResourceLocation(soundId))
        if (soundEvent != null) {
            // 播放声音
            entity.level().playSound(null, entity.blockPosition(), soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f)
        } else {
            // 声音不存在时使用fallback
            playFallbackSound(entity)
        }
    }

    /**
     * 播放fallback声音
     * 核心机制：随机选择一个游戏内置声音
     */
    private fun playFallbackSound(entity: Entity) {
        // 可用的fallback声音列表
        val fallbackSounds = arrayOf(
            SoundEvents.CREEPER_HURT,
            SoundEvents.CREEPER_DEATH,
            SoundEvents.CREEPER_PRIMED,
            SoundEvents.CHICKEN_HURT,
            SoundEvents.CHICKEN_AMBIENT,
        )
        // 随机选择一个声音
        val randomSound = fallbackSounds[Random.nextInt(fallbackSounds.size)]
        // 播放声音
        entity.level().playSound(null, entity.blockPosition(), randomSound, SoundSource.PLAYERS, 1.0f, 1.0f)
    }
}
