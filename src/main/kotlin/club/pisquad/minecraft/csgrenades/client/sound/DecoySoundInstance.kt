package club.pisquad.minecraft.csgrenades.client.sound

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.getTimeFromTickCount
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3

abstract class DecoySoundInstance(pos: Vec3, val lifetime: Double, soundEvent: SoundEvent, soundSource: SoundSource, randomSource: RandomSource) : AbstractTickableSoundInstance(soundEvent, soundSource, randomSource) {
    init {
        this.x = pos.x
        this.y = pos.y
        this.z = pos.z
    }
}

class SimpleDecoySoundInstance(pos: Vec3, lifetime: Double, randomSource: RandomSource) :
    DecoySoundInstance(
        pos,
        lifetime,
        getRandomSoundEvent(randomSource),
        SoundSource.NEUTRAL,
        randomSource,
    ) {
    var tickCount = 0

    init {
        this.looping = true
        this.pitch = 1f
        this.volume = 1f
        this.attenuation = SoundInstance.Attenuation.NONE
    }

    companion object {
        private fun getRandomSoundEvent(randomSource: RandomSource): SoundEvent = listOf<SoundEvent>(
            SoundEvents.CREEPER_PRIMED,
            SoundEvents.ANVIL_USE,
        ).random()

        fun generate(pos: Vec3): SimpleDecoySoundInstance {
            val randomSource = RandomSource.create()
            return SimpleDecoySoundInstance(pos, ModConfig.Decoy.LIFETIME.get(), randomSource)
        }
    }

    override fun tick() {
        if (getTimeFromTickCount(tickCount.toDouble()) > this.lifetime) {
            this.stop()
        }
        tickCount++
    }
}
