package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.grenades.decoy.DecoyRegistries
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryRegistries
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovRegistries
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangRegistries
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.HEGrenadeRegistries
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeRegistries
import net.minecraftforge.eventbus.api.IEventBus

object RegistryHelper {
    // These helps load the object early before register happens
    private val _hegrenade = HEGrenadeRegistries
    private val _smokegrenade = SmokeGrenadeRegistries
    private val _flashbang = FlashbangRegistries
    private val _decoy = DecoyRegistries
    private val _incendiary = IncendiaryRegistries
    private val _molotov = MolotovRegistries

    fun registerMod(bus: IEventBus) {
        ModItems.register(bus)
        ModEntities.register(bus)
        ModParticles.register(bus)
        ModCreativeTabs.register(bus)
        ModSoundEvents.register(bus)
    }
}