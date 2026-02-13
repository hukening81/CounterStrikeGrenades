package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.command.ModCommands
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.registry.*
import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.KotlinModLoadingContext
import thedarkcolour.kotlinforforge.forge.registerConfig

/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in mods.toml.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */

@Mod(CounterStrikeGrenades.ID)
object CounterStrikeGrenades {

    const val ID = "csgrenades"

    // the logger for our mod
    val Logger: Logger = LogManager.getLogger(ID)

    init {

        Logger.log(Level.INFO, "Hello Counter Strike Grenades")

        val modBus = KotlinModLoadingContext.get().getKEventBus()
        val forgeBus = MinecraftForge.EVENT_BUS

        ModEntities.ENTITIES.register(modBus)
        ModItems.ITEMS.register(modBus)
        ModSoundEvents.register(modBus)
        ModParticles.PARTICLE_TYPES.register(modBus)
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modBus)
        modBus.addListener(::removeFromCreativeTabs)

        ModPacketHandler.registerMessage()

        forgeBus.register(ModCommands)

        ModSerializers.register()
        registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, ModConfig.SPEC)
    }

    private fun removeFromCreativeTabs(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey == CreativeModeTabs.COMBAT) {
            val itemsToRemove = setOf(
                ModItems.HEGRENADE_ITEM.get(),
                ModItems.SMOKE_GRENADE_ITEM.get(),
                ModItems.FLASH_BANG_ITEM.get(),
                ModItems.DECOY_GRENADE_ITEM.get(),
                ModItems.MOLOTOV_ITEM.get(),
                ModItems.INCENDIARY_ITEM.get(),
            )

            // Correct way: Use an iterator on the entry set to safely remove.
            val iterator = event.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry.key.item in itemsToRemove) {
                    iterator.remove()
                }
            }
        }
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        Logger.log(Level.INFO, "Initializing client...")

        val eventBus = KotlinModLoadingContext.get().getKEventBus()

        eventBus.addListener(ModParticleFactories::onRegisterParticleFactories)
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        val forgeBus = MinecraftForge.EVENT_BUS
        CounterStrikeGrenadeEntity.registerGrenadeEntityEventHandler(forgeBus)
        Logger.log(Level.INFO, "Server starting...")
    }
}
