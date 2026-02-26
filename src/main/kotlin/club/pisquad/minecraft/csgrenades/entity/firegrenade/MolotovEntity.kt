package club.pisquad.minecraft.csgrenades.entity.firegrenade

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registry.ModDamageType
import net.minecraft.core.registries.Registries
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level

class MolotovEntity(pEntityType: EntityType<out MolotovEntity>, pLevel: Level) : FireGrenadeEntity(pEntityType, pLevel, GrenadeType.MOLOTOV) {
    override fun getHitDamageSource(hitEntity: LivingEntity): DamageSource {
        val registryAccess = this.level().registryAccess()
        val damageTypeHolder = registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.MOLOTOV_HIT)
        return if (hitEntity == this.ownerUuid) {
            DamageSource(damageTypeHolder, this)
        } else {
//            DamageSource(damageTypeHolder, this, this.ownerUuid)
            DamageSource(damageTypeHolder, this, null)
        }
    }
}
