package club.pisquad.minecraft.csgrenades.registry.sounds

import club.pisquad.minecraft.csgrenades.CommonSoundVolumes
import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.ModLogger
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModSoundEvents {
    val SOUND_EVENTS: DeferredRegister<SoundEvent> =
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CounterStrikeGrenades.ID)

    val common = CommonSoundEvents
    val hegrenade = HEGrenadeSoundEvents
    val flashbang = FlashbangSoundEvents
    val decoy = DecoySoundEvents
    val smokegrenade = SmokeGrenadeSoundEvents
    val molotov = MolotovSoundEvents
    val incendiary = IncendiarySoundEvents

    fun register(bus: IEventBus) {
        ModLogger.info("Registering sound events")
        SOUND_EVENTS.register(bus)
    }

    fun registerSoundEvent(name: String): RegistryObject<SoundEvent> {
        // Don't know why forge use the name in json file instead of the actual file location for ResourceLocation
        return SOUND_EVENTS.register(name) {
            SoundEvent.createVariableRangeEvent(ResourceLocation(CounterStrikeGrenades.ID, name))
        }
    }
}

data class GrenadeSoundData(
    val sound: RegistryObject<SoundEvent>,
    val volume: Float,
) {
    companion object {
        fun create(name: String, volume: Double = 1.0): GrenadeSoundData {
            return GrenadeSoundData(
                ModSoundEvents.registerSoundEvent(name),
                volume.toFloat()
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
