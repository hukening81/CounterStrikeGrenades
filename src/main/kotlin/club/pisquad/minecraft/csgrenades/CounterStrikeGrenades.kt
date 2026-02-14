package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.config.*
import club.pisquad.minecraft.csgrenades.network.*
import club.pisquad.minecraft.csgrenades.registry.*
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

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
        Logger.log(Level.INFO, "Hello Counter Strike Grenades")
//        val modBus = KotlinModLoadingContext.get().getKEventBus()
        val modBus = context.modEventBus
        modEventBus = modBus
        modLoadingContext = context

        ModItems.register(modEventBus)
        ModEntities.register(modEventBus)
        ModParticles.register(modEventBus)
        ModCreativeTabs.register(modEventBus)
        ModSoundEvents.register(modEventBus)
        ModPacketHandler.register()

        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, ModConfig.SPEC)
    }

    companion object {
        const val ID = "csgrenades"

        // the logger for our mod
        val Logger: Logger = LogManager.getLogger(ID)

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
            Logger.log(Level.INFO, "Initializing client...")
        }

        /**
         * Fired on the global Forge bus.
         */
        @JvmStatic
        @SubscribeEvent
        fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
            MinecraftForge.EVENT_BUS
            Logger.log(Level.INFO, "Server starting...")
        }
    }
}
