package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.client.sound.DecoySoundInstance
import club.pisquad.minecraft.csgrenades.client.sound.SimpleDecoySoundInstance
import club.pisquad.minecraft.csgrenades.compat.tacz.TaczApiHandler
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.getTimeFromTickCount
import club.pisquad.minecraft.csgrenades.registry.ModDamageType
import club.pisquad.minecraft.csgrenades.registry.ModItems
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.Registries
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class DecoyGrenadeEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) : CounterStrikeGrenadeEntity(pEntityType, pLevel, GrenadeType.DECOY_GRENADE) {

    private var soundInstance: DecoySoundInstance? = null
    private var tickSinceActivate = 0

    companion object {}

    override fun defineSynchedData() {
        super.defineSynchedData()
    }

    override fun getDefaultItem(): Item = ModItems.DECOY_GRENADE_ITEM.get()

    override fun onRemovedFromWorld() {
        super.onRemovedFromWorld()
//        if (this.level().isClientSide && soundInstance != null) {
        if (soundInstance != null) {
            Minecraft.getInstance().soundManager.stop(soundInstance)
        }
//        }
    }

    override fun tick() {
        super.tick()
        // Temporary solution because these two somehow are not in sync
        // States of these two should be identical since a grenade activates when landed on the ground.
        // And if it activates in the air, because it will be kill right away, that shouldn't matter.
        if (this.entityData.get(isLandedAccessor) && !this.entityData.get(isActivatedAccessor)) {
            this.entityData.set(isActivatedAccessor, true)
            this.activate()
        }
        if (this.entityData.get(isActivatedAccessor)) {
            tickSinceActivate++
            if (getTimeFromTickCount(tickSinceActivate.toDouble()) > ModConfig.Decoy.LIFETIME.get()) {
                this.endOfLifeExplosion()
                this.discard()
            }
        }
    }

    override fun activate() {
        // Play fake sound
        if (this.entityData.get(isActivatedAccessor)) {
            if (this.level().isClientSide) {
                // EMPTY
            } else {
                // SoundInstances played on server side should be automatically synced to client side
                if (this.soundInstance == null) {
                    this.soundInstance = getDecoySoundInstance(this.center)
                    Minecraft.getInstance().soundManager.play(soundInstance)
                } else {
                    // EMPTY
                }
            }
        }
    }

    override fun getHitDamageSource(hitEntity: LivingEntity): DamageSource {
        val registryAccess = this.level().registryAccess()
        val damageTypeHolder = registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.DECOY_GRENADE_HIT)
        return if (hitEntity == this.owner) {
            DamageSource(damageTypeHolder, this)
        } else {
            DamageSource(damageTypeHolder, this, this.owner)
        }
    }

    private fun endOfLifeExplosion() {
        if (!level().isClientSide) {
            this.level().explode(this, this.x, this.y, this.z, 0.1f, false, Level.ExplosionInteraction.NONE)
        }
        this.discard()
    }
}

private fun getDecoySoundInstance(pos: Vec3): DecoySoundInstance = if (TaczApiHandler.isLoaded()) {
    SimpleDecoySoundInstance.generate(pos)
} else {
    SimpleDecoySoundInstance.generate(pos)
}
