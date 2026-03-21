package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.registry.RegistryHelper
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in mods.toml.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */

@Mod(CounterStrikeGrenades.ID)
class CounterStrikeGrenades(context: FMLJavaModLoadingContext) {

    init {
        ModLogger.info("Initializing Mod -- Common Setup")
//        val modBus = KotlinModLoadingContext.get().getKEventBus()
        val modBus = context.modEventBus
        modEventBus = modBus
        modLoadingContext = context
        
        RegistryHelper.registerMod(modEventBus)

        ModPacketHandler.register()

        ModLogger.info("Generating/Reading server-side config")
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, ModConfig.SPEC)
    }

    companion object {
        const val ID = "csgrenades"
        lateinit var modEventBus: IEventBus
        lateinit var modLoadingContext: FMLJavaModLoadingContext

        /**
         * This is used for initializing client specific
         * things such as renderers and keymaps
         * Fired on the mod specific event bus.
         */
        @JvmStatic
        @SubscribeEvent
        fun onClientSetup(event: FMLClientSetupEvent) {
            ModLogger.info("Initializing Mod -- Client Setup")
        }

        /**
         * Fired on the global Forge bus.
         */
        @JvmStatic
        @SubscribeEvent
        fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
            ModLogger.info("Initializing Mod -- Server Setup")
        }
    }
}
