package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.GrenadeImplementation
import club.pisquad.minecraft.csgrenades.grenades.decoy.DecoyImplementation
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryImplementation
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovImplementation
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangImplementation
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.HEGrenadeImplementation
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeImplementation

interface WithGrenadeType {
    val grenadeType: GrenadeType
}

enum class GrenadeType(
    val implementation: GrenadeImplementation,
) {
    DECOY(
        DecoyImplementation
    ),
    FLASH_BANG(
        FlashbangImplementation
    ),
    SMOKE_GRENADE(
        SmokeGrenadeImplementation,
    ),
    HE_GRENADE(
        HEGrenadeImplementation
    ),
    INCENDIARY(
        IncendiaryImplementation,
    ),
    MOLOTOV(
        MolotovImplementation,
    );

    val resourceKey: String = implementation.resourceKey
}

enum class ThrowType(
    val getSpeed: () -> Double
) {
    WEAK({ ModConfig.throwConfig.speed_weak.get().toMetersPerTick() }),
    MEDIUM({ ModConfig.throwConfig.speed_medium.get().toMetersPerTick() }),
    STRONG({ ModConfig.throwConfig.speed_strong.get().toMetersPerTick() }),
}