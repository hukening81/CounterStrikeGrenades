package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.entity.impl.ActivateAfterLandingGrenadeEntity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class DecoyGrenadeEntity(
    pEntityType: EntityType<out DecoyGrenadeEntity>,
    pLevel: Level,
) : ActivateAfterLandingGrenadeEntity(
    pEntityType,
    pLevel,
//    ModConfig.decoy.grenadeCommonConfig.fuseTime.get().toTick().toInt(),
    20 * 60
) {
    override val sounds = DecoyRegistries.sounds
    override val damageTypes = DecoyRegistries.damageTypes
    override val grenadeType: GrenadeType = GrenadeType.DECOY
}