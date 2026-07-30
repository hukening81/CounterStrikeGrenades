package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.GrenadeProperties
import club.pisquad.minecraft.csgrenades.grenades.decoy.DecoyProperties
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryProperties
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovProperties
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangProperties
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.HEGrenadeProperties
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.CTSmokeGrenadeProperties
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.TSmokeGrenadeProperties

interface WithGrenadeType {
    val grenadeType: GrenadeType
}

enum class GrenadeType(
    val properties: GrenadeProperties,
) {
    DECOY(DecoyProperties),
    FLASH_BANG(FlashbangProperties),
    CT_SMOKE(CTSmokeGrenadeProperties),
    T_SMOKE(TSmokeGrenadeProperties),
    HE_GRENADE(HEGrenadeProperties),
    INCENDIARY(IncendiaryProperties),
    MOLOTOV(MolotovProperties);

    val resourceKey: String
        get() {
            return this.properties.resourceKey
        }
}

enum class ThrowType(
    val getSpeed: () -> Double
) {
    WEAK({
        ModConfig.throwConfig.speed_weak.get().toMetersPerTick()
    }),
    MEDIUM({
        ModConfig.throwConfig.speed_medium.get().toMetersPerTick()
    }),
    STRONG({ ModConfig.throwConfig.speed_strong.get().toMetersPerTick() }),
}