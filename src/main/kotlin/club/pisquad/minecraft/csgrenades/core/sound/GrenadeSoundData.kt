package club.pisquad.minecraft.csgrenades.core.sound

import club.pisquad.minecraft.csgrenades.CommonSoundVolumes
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3
import net.minecraftforge.registries.RegistryObject


class GrenadeSoundData(
    val soundRegistry: RegistryObject<SoundEvent>,
    val volumeD: Double,
    val source: SoundSource = SoundSource.PLAYERS
) {
    val soundEvent: SoundEvent
        get() = soundRegistry.get()
    val volume: Float
        get() = volumeD.toFloat()

    companion object {
        fun create(name: String, volume: Double = 1.0): GrenadeSoundData {
            return GrenadeSoundData(
                ModSoundEvents.registerSoundEvent(name),
                volume
            )
        }

        fun createThrow(name: String, volume: Double = CommonSoundVolumes.THROW): GrenadeSoundData {
            return create(name, volume)
        }

        fun createDraw(name: String, volume: Double = CommonSoundVolumes.DRAW): GrenadeSoundData {
            return create(name, volume)
        }

        fun createHitBlock(name: String, volume: Double = CommonSoundVolumes.HIT_BLOCK): GrenadeSoundData {
            return create(name, volume)
        }
    }
}

class DistanceSegmentedSoundData(vararg val ranges: Pair<Double, GrenadeSoundData>) {
    fun getInstance(position: Vec3, distance: Double): SoundInstance? {
        for (section in ranges) {
            if (distance < section.first) {
                val data = section.second
                return SimpleSoundInstance(
                    data.soundEvent,
                    data.source,
                    data.volume,
                    1.0f,
                    RandomSource.create(),
                    position.x,
                    position.y,
                    position.z
                )
            }
        }
        return null
    }
}