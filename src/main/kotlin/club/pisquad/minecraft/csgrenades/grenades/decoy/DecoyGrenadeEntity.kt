package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.entity.ActivateAfterLandingGrenadeEntity
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class DecoyGrenadeEntity(
    pEntityType: EntityType<out DecoyGrenadeEntity>,
    pLevel: Level,
) : ActivateAfterLandingGrenadeEntity(
    pEntityType,
    pLevel,
    GrenadeType.DECOY,
    ModConfig.decoy.grenadeCommonConfig.fuseTime.get().toTick().toInt(),
) {
    override val sounds = DecoyRegistries.sounds
    override val damageTypes = DecoyRegistries.damageTypes
}