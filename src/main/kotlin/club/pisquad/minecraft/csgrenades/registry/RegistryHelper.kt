package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.GrenadeType
import net.minecraftforge.eventbus.api.IEventBus

object RegistryHelper {
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