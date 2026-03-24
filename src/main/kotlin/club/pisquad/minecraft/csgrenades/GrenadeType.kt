package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.grenades.decoy.DecoyRegistries
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryRegistries
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovRegistries
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangRegistries
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.HEGrenadeRegistries
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeRegistries
import net.minecraft.world.entity.EntityType

enum class GrenadeType(val resourceKey: String) {
    FLASH_BANG("flashbang"),
    SMOKE_GRENADE("smokegrenade"),
    HE_GRENADE("hegrenade"),
    INCENDIARY("incendiary"),
    MOLOTOV("molotov"),
    DECOY("decoy"),

}

fun GrenadeType.getEntity(): EntityType<out CounterStrikeGrenadeEntity> {
    return when (this) {
        GrenadeType.FLASH_BANG -> FlashbangRegistries.entity.get()
        GrenadeType.SMOKE_GRENADE -> SmokeGrenadeRegistries.entity.get()
        GrenadeType.HE_GRENADE -> HEGrenadeRegistries.entity.get()
        GrenadeType.INCENDIARY -> IncendiaryRegistries.entity.get()
        GrenadeType.MOLOTOV -> MolotovRegistries.entity.get()
        GrenadeType.DECOY -> DecoyRegistries.entity.get()
    }
}

fun GrenadeType.getItem(): CounterStrikeGrenadeItem {
    return when (this) {
        GrenadeType.FLASH_BANG -> FlashbangRegistries.item.get()
        GrenadeType.SMOKE_GRENADE -> SmokeGrenadeRegistries.item.get()
        GrenadeType.HE_GRENADE -> HEGrenadeRegistries.item.get()
        GrenadeType.INCENDIARY -> IncendiaryRegistries.item.get()
        GrenadeType.MOLOTOV -> MolotovRegistries.item.get()
        GrenadeType.DECOY -> DecoyRegistries.item.get()
    }
}