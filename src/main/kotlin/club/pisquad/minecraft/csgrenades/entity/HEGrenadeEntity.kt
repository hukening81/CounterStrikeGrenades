package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.client.renderer.HEGrenadeExplosionData
import club.pisquad.minecraft.csgrenades.client.renderer.HEGrenadeRenderManager
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.getTimeFromTickCount
import club.pisquad.minecraft.csgrenades.registery.ModDamageType
import club.pisquad.minecraft.csgrenades.registery.ModItems
import club.pisquad.minecraft.csgrenades.registery.ModSoundEvents
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import kotlin.math.max

class HEGrenadeEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) :
    CounterStrikeGrenadeEntity(pEntityType, pLevel, GrenadeType.FLASH_BANG) {

    init {
        this.hitBlockSound = ModSoundEvents.HEGRENADE_BOUNCE.get()
    }

    override fun getDefaultItem(): Item {
        return ModItems.HEGRENADE_ITEM.get()
    }

    override fun tick() {
        super.tick()

        // Explosion logic
        if (getTimeFromTickCount(this.tickCount.toDouble()) > 2.5 && !this.entityData.get(isExplodedAccessor)) {
            if (!this.level().isClientSide) { // Server
                this.doDamage()
                // [注释] 这是一个解决模型渲染问题的非常规方案 (Teleport Hack)。
                // 因为常规的隐形方法(isInvisible)对该实体无效，所以在爆炸时，服务器将实体传送到地底深处，
                // 使其在视觉上消失。但实体本身会继续存在一段时间，以作为粒子效果的“锚点”。
                this.setPos(this.x, -1000.0, this.z) // Teleport away
            } else { // Client
                HEGrenadeRenderManager.render(HEGrenadeExplosionData(this.position()))
                this.blowUpNearbySmokeGrenade()
            }
            this.entityData.set(isExplodedAccessor, true)
        }

        // Delayed removal on server
        // [注释] 爆炸若干秒后，服务器会将实体彻底移除。这个延迟是为了确保客户端有足够的时间播放完粒子效果。
        if (this.entityData.get(isExplodedAccessor) && getTimeFromTickCount(this.tickCount.toDouble()) > 4.5) { // 2s after explosion
            if (!this.level().isClientSide) {
                this.discard()
            }
        }
    }

    private fun doDamage() {
        val level = this.level() as ServerLevel
        val registryAccess = this.level().registryAccess()
        val damageRange = ModConfig.HEGrenade.DAMAGE_RANGE.get()
        val damageSource = DamageSource(
            registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.HEGRENADE_EXPLOSION),
            this.owner
        )
        val entities =
            level.getEntitiesOfClass(
                if (ModConfig.DAMAGE_NON_PLAYER_ENTITY.get()) LivingEntity::class.java else Player::class.java,
                AABB(this.blockPosition()).inflate(ModConfig.HEGrenade.DAMAGE_RANGE.get())
            )
        for (entity in entities) {
            val distance = entity.distanceTo(this).toDouble()

            if (distance < damageRange) {
                val damage = getDamageBlockingState(entity)
                if (damage > 0.0) {
                    val originalKnockBackResistance =
                        entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE)?.baseValue ?: 0.0
                    entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE)?.baseValue = 1.0
                    entity.hurt(damageSource, damage.toFloat())
                    entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE)?.baseValue = originalKnockBackResistance
                }
            }
        }
    }

    private fun getDamageBlockingState(entity: LivingEntity): Double {
        val headDamage = ClipContext(
            this.position(),
            entity.eyePosition,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.ANY,
            null
        ).let {
            val clipResult = this.level().clip(it)
            return@let if (clipResult.type == HitResult.Type.MISS) {
                val distance = this.position().distanceTo(entity.eyePosition)
                return@let if (distance < 1.5) {
                    calculateHEGrenadeDamage(distance, 0.0, true)
                } else {
                    calculateHEGrenadeDamage(distance, 0.0)
                }
            } else {
                0.0
            }
        }

        val bodyDamage = ClipContext(
            this.position(),
            entity.position(),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.ANY,
            null
        ).let {
            val clipResult = this.level().clip(it)
            return@let if (clipResult.type == HitResult.Type.MISS) {
                calculateHEGrenadeDamage(this.position().distanceTo(entity.position()), 0.0)
            } else {
                0.0
            }
        }

        return max(headDamage, bodyDamage)
    }

    override fun getHitDamageSource(): DamageSource {
        val registryAccess = this.level().registryAccess()
        return DamageSource(
            registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.HEGRENADE_HIT),
            this
        )
    }

    private fun blowUpNearbySmokeGrenade() {
        val smokeRadius = ModConfig.SmokeGrenade.SMOKE_RADIUS.get()
        val heDamageRange = ModConfig.HEGrenade.DAMAGE_RANGE.get()
        val smokeFallingHeight = ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get()
        this.level().getEntitiesOfClass(
            SmokeGrenadeEntity::class.java,
            this.boundingBox.inflate(heDamageRange + smokeRadius)
                .inflate(0.0, smokeFallingHeight.toDouble(), 0.0)
        ).forEach {
            it.clearSmokeWithinRange(this.position(), heDamageRange + 2.5)
        }
    }
}

private fun calculateHEGrenadeDamage(
    distance: Double,
    armorReduction: Double,
    headDamageBoost: Boolean = false
): Double {
    val baseDamage =
        if (headDamageBoost) ModConfig.HEGrenade.BASE_DAMAGE.get() * ModConfig.HEGrenade.HEAD_DAMAGE_BOOST.get() else ModConfig.HEGrenade.BASE_DAMAGE.get()
    val damageRange = ModConfig.HEGrenade.DAMAGE_RANGE.get()
    return baseDamage.times(1.0.minus(distance.div(damageRange)))
        .times(1.0.minus(armorReduction))
}