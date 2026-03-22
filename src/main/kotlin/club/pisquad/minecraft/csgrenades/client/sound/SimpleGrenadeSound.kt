package club.pisquad.minecraft.csgrenades.client.sound

import club.pisquad.minecraft.csgrenades.CommonSoundVolumes
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3
import net.minecraftforge.registries.RegistryObject
import kotlin.random.Random

data class SoundContext(
    val position: Vec3,
) {
    companion object {
        fun playerEarPos(): Vec3 {
            // Since all sound relative utils only used on client side, following assertion is safe
            return Minecraft.getInstance().player!!.position()
        }

    }

    fun distanceFromPlayer(): Double {
        return playerEarPos().distanceTo(this.position)
    }

}

interface GrenadeSound {
    fun getSoundInstance(context: SoundContext): SoundInstance
}

class SimpleGrenadeSound(
    private val soundRegistry: RegistryObject<SoundEvent>,
    private val volume: Double,
) : GrenadeSound {
    override fun getSoundInstance(context: SoundContext): SoundInstance {
        return SimpleSoundInstance(
            soundRegistry.get(),
            SoundSource.PLAYERS,
            volume.toFloat(),
            1f,
            RandomSource.create(Random.nextLong()),
            context.position.x,
            context.position.y,
            context.position.z,
        )
    }

    companion object {
        fun create(name: String, volume: Double = 1.0): SimpleGrenadeSound {
            return SimpleGrenadeSound(
                ModSoundEvents.registerSoundEvent(name),
                volume
            )
        }

        fun createThrow(name: String, volume: Double = CommonSoundVolumes.THROW): SimpleGrenadeSound {
            return create(name, volume)
        }

        fun createDraw(name: String, volume: Double = CommonSoundVolumes.DRAW): SimpleGrenadeSound {
            return create(name, volume)
        }

        fun createHitBlock(name: String, volume: Double = CommonSoundVolumes.HIT_BLOCK): SimpleGrenadeSound {
            return create(name, volume)
        }
    }
}

class DistanceBasedGrenadeSound(vararg val ranges: Pair<RegistryObject<SoundEvent>, Double>) : GrenadeSound {

    override fun getSoundInstance(context: SoundContext): SoundInstance {
        val distance = context.distanceFromPlayer()
        ranges.forEachIndexed { index, pair ->
            if (index == ranges.size - 1 || distance < ranges[index + 1].second) {
                return SimpleSoundInstance(
                    pair.first.get(),
                    SoundSource.PLAYERS,
                    pair.second,
                )
            }
        }
    }

}