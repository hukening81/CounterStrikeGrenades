package club.pisquad.minecraft.csgrenades.entity.decoy

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.entity.core.ActivateAfterLandingGrenadeEntity
import club.pisquad.minecraft.csgrenades.entity.core.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.entity.core.GrenadeEntitySoundEvents
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registry.damage.ModDamageTypes
import club.pisquad.minecraft.csgrenades.registry.sounds.DecoySoundEvents
import club.pisquad.minecraft.csgrenades.registry.sounds.FlashbangSoundEvents
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
    override val sounds = GrenadeEntitySoundEvents(
        DecoySoundEvents.throwSound,
        FlashbangSoundEvents.hitBlock,
    )
    override val damageTypes = GrenadeEntityDamageTypes(
        ModDamageTypes.decoy.hit,
        ModDamageTypes.decoy.explosion,
    )
}
