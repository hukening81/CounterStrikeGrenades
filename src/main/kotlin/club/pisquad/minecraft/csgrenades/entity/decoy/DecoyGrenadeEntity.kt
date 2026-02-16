package club.pisquad.minecraft.csgrenades.entity.decoy

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.entity.ActivateAfterLandingGrenadeEntity
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registry.ModDamageType
import club.pisquad.minecraft.csgrenades.registry.ModItems
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.core.registries.Registries
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class DecoyGrenadeEntity(
    pEntityType: EntityType<out ThrowableItemProjectile>,
    pLevel: Level,
) : ActivateAfterLandingGrenadeEntity(pEntityType, pLevel, GrenadeType.DECOY, ModConfig.Decoy.FUSE_TIME_AFTER_LANDING.get().toTick().toInt()) {
    override fun getHitDamageSource(hitEntity: LivingEntity): DamageSource {
        val registryAccess = this.level().registryAccess()
        val damageTypeHolder = registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.DECOY_GRENADE_HIT)
        return if (hitEntity == this.owner) {
            DamageSource(damageTypeHolder, this)
        } else {
            DamageSource(damageTypeHolder, this, this.owner)
        }
    }

    override fun getDefaultItem(): Item = ModItems.DECOY_GRENADE_ITEM.get()
}
