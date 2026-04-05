package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.entity.impl.ActivateAfterLandingGrenadeEntity
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class SmokeGrenadeEntity(pEntityType: EntityType<out SmokeGrenadeEntity>, pLevel: Level) :
    ActivateAfterLandingGrenadeEntity(
        pEntityType,
        pLevel,
        GrenadeType.SMOKE_GRENADE,
        ModConfig.smokegrenade.grenadeCommonConfig.fuseTime.get().toTick().toInt(),
    ) {
    override val sounds = SmokeGrenadeRegistries.sounds
    override val damageTypes = SmokeGrenadeRegistries.damageTypes
}
