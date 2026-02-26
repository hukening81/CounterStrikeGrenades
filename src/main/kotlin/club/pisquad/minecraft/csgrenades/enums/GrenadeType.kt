package club.pisquad.minecraft.csgrenades.enums

import club.pisquad.minecraft.csgrenades.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.registry.ModEntities
import net.minecraft.world.entity.EntityType

enum class GrenadeType(val resourceKey: String) {
    FLASH_BANG("flashbang"),
    SMOKE_GRENADE("smokegrenade"),
    HE_GRENADE("hegrenade"),
    INCENDIARY("incendiary"),
    MOLOTOV("molotov"),
    DECOY("decoy"),
}

fun GrenadeType.toModEntity(): EntityType<out CounterStrikeGrenadeEntity> {
    return when (this) {
        GrenadeType.FLASH_BANG -> ModEntities.FLASH_BANG_ENTITY.get()
        GrenadeType.SMOKE_GRENADE -> ModEntities.SMOKE_GRENADE_ENTITY.get()
        GrenadeType.HE_GRENADE -> ModEntities.HEGRENADE_ENTITY.get()
        GrenadeType.INCENDIARY -> ModEntities.INCENDIARY_ENTITY.get()
        GrenadeType.MOLOTOV -> ModEntities.MOLOTOV_ENTITY.get()
        GrenadeType.DECOY -> ModEntities.DECOY_ENTITY.get()
    }
}
