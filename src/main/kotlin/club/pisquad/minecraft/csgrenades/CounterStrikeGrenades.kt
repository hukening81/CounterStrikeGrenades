package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.compat.CSGrenadeCompatibility
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.grenades.decoy.DecoyRegistryHelper
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeRegistryHelper
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangRegistryHelper
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.HEGrenadeRegistryHelper
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeRegistryHelper
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.registry.RegistryHelper
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Suppress("UnusedExpression")
private fun loadAllGrenades() {
    HEGrenadeRegistryHelper
    DecoyRegistryHelper
    SmokeRegistryHelper
    FlashbangRegistryHelper
    FireGrenadeRegistryHelper
}

@Mod(CounterStrikeGrenades.ID)
class CounterStrikeGrenades(context: FMLJavaModLoadingContext) {

    init {
        ModLogger.info("Initializing Mod -- Common Setup")

        loadAllGrenades()

        CSGrenadeCompatibility.supportedMods

        RegistryHelper.commonSetup(context.modEventBus)

        ModPacketHandler.register()

        ModLogger.info("Generating/Reading server-side config")
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, ModConfig.build())
    }

    companion object {
        public const val ID: String = "csgrenades"

        @JvmStatic
        @SubscribeEvent
        @Suppress("unused")
        fun onClientSetup(event: FMLClientSetupEvent) {
            ModLogger.info("Initializing Mod -- Client Setup")
        }

        @JvmStatic
        @SubscribeEvent
        @Suppress("unused")
        fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
            ModLogger.info("Initializing Mod -- Server Setup")
        }
    }
}
