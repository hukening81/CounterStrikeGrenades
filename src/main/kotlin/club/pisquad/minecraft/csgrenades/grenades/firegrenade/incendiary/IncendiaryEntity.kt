package club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeEntity
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeVariant
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class IncendiaryEntity(entityType: EntityType<IncendiaryEntity>, pLevel: Level) : FireGrenadeEntity(
    entityType, pLevel,
    IncendiaryConfig.common.fuseTime.get(),
) {
    override val sounds = IncendiarySounds
    override val damageTypes = IncendiaryDamageTypes
    override val grenadeType: GrenadeType = GrenadeType.INCENDIARY
    override val variant: FireGrenadeVariant = FireGrenadeVariant.INCENDIARY
}