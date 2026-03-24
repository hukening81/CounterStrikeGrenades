package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.grenades.decoy.DecoyRegistries
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryRegistries
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovRegistries
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangRegistries
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.HEGrenadeRegistries
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import net.minecraft.world.entity.EntityType
import java.util.function.Supplier

enum class GrenadeType(
    val resourceKey: String,
    // Use supplier to avoid circular initialization
    val entity: Supplier<out EntityType<out CounterStrikeGrenadeEntity>>,
    val item: Supplier<out CounterStrikeGrenadeItem>,
    val sounds: Supplier<out GrenadeSoundEvents>,
) {
    FLASH_BANG(
        "flashbang",
        { FlashbangRegistries.entity.get() },
        { FlashbangRegistries.item.get() },
        { FlashbangRegistries.sounds }
    ),
    SMOKE_GRENADE(
        "smokegrenade",
        { SmokeGrenadeRegistries.entity.get() },
        { SmokeGrenadeRegistries.item.get() },
        { SmokeGrenadeRegistries.sounds },
    ),
    HE_GRENADE(
        "hegrenade",
        { HEGrenadeRegistries.entity.get() },
        { HEGrenadeRegistries.item.get() },
        { HEGrenadeRegistries.sounds }
    ),
    INCENDIARY(
        "incendiary",
        { IncendiaryRegistries.entity.get() },
        { IncendiaryRegistries.item.get() },
        { IncendiaryRegistries.sounds },
    ),
    MOLOTOV(
        "molotov",
        { MolotovRegistries.entity.get() },
        { MolotovRegistries.item.get() },
        { MolotovRegistries.sounds },
    ),
    DECOY(
        "decoy",
        { DecoyRegistries.entity.get() },
        { DecoyRegistries.item.get() },
        { DecoyRegistries.sounds }
    ),
}