package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.GrenadeType
import net.minecraftforge.eventbus.api.IEventBus

object RegistryHelper {
    // These helps load the object early before register happens
//    private val _hegrenade = HEGrenadeRegistries
//    private val _smokegrenade = SmokeGrenadeRegistries
//    private val _flashbang = FlashbangRegistries
//    private val _decoy = DecoyRegistries
//    private val _incendiary = IncendiaryRegistries
//    private val _molotov = MolotovRegistries

    fun commonSetup(modBus: IEventBus) {
        ModItems.register(modBus)
        ModEntities.register(modBus)
        ModParticles.register(modBus)
        ModCreativeTabs.register(modBus)
        ModSoundEvents.register(modBus)

        GrenadeType.entries.forEach {
            it.registries.get().registerSerializers()
        }
    }
}