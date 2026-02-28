package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.item.decoy.DecoyGrenadeItem
import club.pisquad.minecraft.csgrenades.item.flashbang.FlashBangItem
import club.pisquad.minecraft.csgrenades.item.hegrenade.HEGrenadeItem
import club.pisquad.minecraft.csgrenades.item.incendiary.IncendiaryItem
import club.pisquad.minecraft.csgrenades.item.molotov.MolotovItem
import club.pisquad.minecraft.csgrenades.item.smokegrenade.SmokeGrenadeItem
import net.minecraft.world.item.Item
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModItems {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, CounterStrikeGrenades.ID)

    val FLASH_BANG_ITEM: RegistryObject<Item> = ITEMS.register(GrenadeType.FLASH_BANG.resourceKey) { FlashBangItem(Item.Properties()) }

    val SMOKE_GRENADE_ITEM: RegistryObject<Item> =
        ITEMS.register(GrenadeType.SMOKE_GRENADE.resourceKey) { SmokeGrenadeItem(Item.Properties()) }

    val HEGRENADE_ITEM: RegistryObject<Item> = ITEMS.register(GrenadeType.HE_GRENADE.resourceKey) { HEGrenadeItem(Item.Properties()) }

    val INCENDIARY_ITEM: RegistryObject<Item> = ITEMS.register(GrenadeType.INCENDIARY.resourceKey) { IncendiaryItem(Item.Properties()) }

    val MOLOTOV_ITEM: RegistryObject<Item> = ITEMS.register(GrenadeType.MOLOTOV.resourceKey) { MolotovItem(Item.Properties()) }

    val DECOY_GRENADE_ITEM: RegistryObject<Item> = ITEMS.register(GrenadeType.DECOY.resourceKey) { DecoyGrenadeItem(Item.Properties()) }

    fun register(bus: IEventBus) {
        ITEMS.register(bus)
    }
}
