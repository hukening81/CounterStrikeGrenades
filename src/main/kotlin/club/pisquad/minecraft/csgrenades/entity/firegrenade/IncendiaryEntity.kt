package club.pisquad.minecraft.csgrenades.entity.firegrenade

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.entity.core.GrenadeEntitySoundEvents
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registry.sounds.IncendiarySoundEvents
import club.pisquad.minecraft.csgrenades.registry.sounds.ModSoundEvents
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class IncendiaryEntity(pEntityType: EntityType<out IncendiaryEntity>, pLevel: Level) : FireGrenadeEntity(
    pEntityType, pLevel, GrenadeType.INCENDIARY,
    ModConfig.incendiary.grenadeCommonConfig.fuseTime.get().toTick().toInt(),
) {
    override val sounds = GrenadeEntitySoundEvents(
        IncendiarySoundEvents.throwSound.get(),
        ModSoundEvents.incendiary.bounce.get(),
    )
}
