package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.getItem
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject

object ModCreativeTabs {
    val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CounterStrikeGrenades.ID)

    val CSGRENADES_TAB: RegistryObject<CreativeModeTab> = CREATIVE_MODE_TABS.register("csgrenades_tab") {
        CreativeModeTab.builder()
            .icon { GrenadeType.HE_GRENADE.getItem().defaultInstance }
            .title(Component.translatable("itemGroup.csgrenades"))
            .displayItems { _, output ->
                GrenadeType.entries.forEach { output.accept(it.getItem()) }
//                output.accept(ModItems.HEGRENADE_ITEM.get())
//                output.accept(ModItems.SMOKE_GRENADE_ITEM.get())
//                output.accept(ModItems.FLASH_BANG_ITEM.get())
//                output.accept(ModItems.DECOY_GRENADE_ITEM.get())
//                output.accept(ModItems.MOLOTOV_ITEM.get())
//                output.accept(ModItems.INCENDIARY_ITEM.get())
            }
            .build()
    }

    fun register(bus: IEventBus) {
        ModLogger.info("Registering creative tabs")
        CREATIVE_MODE_TABS.register(bus)
    }
}
