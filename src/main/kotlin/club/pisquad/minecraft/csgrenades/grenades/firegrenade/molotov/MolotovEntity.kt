package club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeEntity
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeVariant
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class MolotovEntity(entityType: EntityType<out MolotovEntity>, pLevel: Level) : FireGrenadeEntity(
    entityType, pLevel, MolotovConfig.common.fuseTime.get(),
) {
    override val sounds = MolotovSounds
    override val damageTypes = MolotovDamageTypes
    override val grenadeType: GrenadeType = GrenadeType.MOLOTOV
    override val variant: FireGrenadeVariant = FireGrenadeVariant.MOLOTOV
}