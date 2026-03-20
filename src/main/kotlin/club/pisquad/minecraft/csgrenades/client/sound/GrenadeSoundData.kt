package club.pisquad.minecraft.csgrenades.client.sound

import club.pisquad.minecraft.csgrenades.CommonSoundVolumes
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.registries.RegistryObject

open class GrenadeSoundData(
    private val soundRegistry: RegistryObject<SoundEvent>,
    private val volume: Double,
) {

    fun getSoundEvent(): SoundEvent {
        return soundRegistry.get()
    }

    fun getVolume(): Float {
        return volume.toFloat()
    }


    companion object {
        fun create(name: String, volume: Double = 1.0): GrenadeSoundData {
            return GrenadeSoundData(
                ModSoundEvents.registerSoundEvent(name),
                volume
            )
        }

        fun createThrow(name: String, volume: Double = CommonSoundVolumes.THROW): GrenadeSoundData {
            return GrenadeSoundData.create(name, volume)
        }

        fun createDraw(name: String, volume: Double = CommonSoundVolumes.DRAW): GrenadeSoundData {
            return GrenadeSoundData.create(name, volume)
        }

        fun createHitBlock(name: String, volume: Double = CommonSoundVolumes.HIT_BLOCK): GrenadeSoundData {
            return GrenadeSoundData.create(name, volume)
        }
    }
}