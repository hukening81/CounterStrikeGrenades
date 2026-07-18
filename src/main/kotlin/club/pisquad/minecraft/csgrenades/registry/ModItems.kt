package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import net.minecraft.world.item.Item
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModItems {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, CounterStrikeGrenades.ID)

    fun register(bus: IEventBus) {
        ModLogger.info("Registering items")
        ITEMS.register(bus)
    }

    fun <I : CounterStrikeGrenadeItem> registerSingle(name: String, factory: () -> I): RegistryObject<I> {
        return ITEMS.register(name, factory)
    }
}
