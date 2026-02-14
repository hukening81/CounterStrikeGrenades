package club.pisquad.minecraft.csgrenades.entity.smokegrenade

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.config.*
import club.pisquad.minecraft.csgrenades.entity.*
import club.pisquad.minecraft.csgrenades.entity.firegrenade.*
import club.pisquad.minecraft.csgrenades.enums.*
import club.pisquad.minecraft.csgrenades.network.*
import club.pisquad.minecraft.csgrenades.network.data.*
import club.pisquad.minecraft.csgrenades.network.message.smokegrenade.*
import club.pisquad.minecraft.csgrenades.registry.*
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.registries.Registries
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import java.time.Duration
import java.time.Instant

class SmokeGrenadeEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) :
    ActivateAfterLandingGrenadeEntity(
        pEntityType,
        pLevel,
        GrenadeType.FLASH_BANG,
        ModConfig.SmokeGrenade.FUSE_TIME_AFTER_LANDING.get().toTick().toInt(),
    ) {

    private var lastPos: Vec3i = Vec3i(0, 0, 0)
    private var explosionTime: Instant? = null

    companion object {
        private val smokeDataPointsAccessor: EntityDataAccessor<Set<SmokeDataPoint>> =
            SynchedEntityData.defineId(SmokeGrenadeEntity::class.java, ModSerializers.smokeDataPointSetSerializer)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        this.entityData.define(smokeDataPointsAccessor, emptySet())
    }

    override fun getDefaultItem(): Item = ModItems.SMOKE_GRENADE_ITEM.get()

    override fun tick() {
        super.tick()
        if (this.entityData.get(isActivatedAccessor)) {
            // EMPTy
        } else {
            if (this.explosionTime != null && Duration.between(
                    this.explosionTime,
                    Instant.now(),
                ) > Duration.ofMillis(
                    ModConfig.SmokeGrenade.SMOKE_LIFETIME.get().toLong(),
                )
            ) {
                this.kill()
            }
            extinguishNearbyFires()
        }
    }

    override fun activate() {
        super.activate()
        val points = SmokeSpreadCalculator(this.level() as ServerLevel, this.center)
            .getResult()
            .toList()
            .map { RoundedVec3.fromVec3(it) }
            .map { SmokeDataPoint(it) }
            .toSet()
        this.entityData.set(smokeDataPointsAccessor, points)
        ModPacketHandler.sendMessageToPlayer(this.level().dimension(), SmokeGrenadeActivatedMessage())
    }

    override fun onAddedToWorld() {
        super.onAddedToWorld()
        lastPos = this.position().toVec3i()
    }

    override fun onHitBlock(result: BlockHitResult) {
        // If the smoke hit the ground with in any incendiary's range, it will emit right away
        super.onHitBlock(result)
        if (result.direction == Direction.UP) {
            if (this.extinguishNearbyFires() > 0) {
//                this.entityData.set(isLandedAccessor, true)
                if (this.level() is ServerLevel && result.isInside) {
                    this.setPos(Vec3(this.position().x, result.blockPos.y + 1.0, this.position().z))
                }
            }
        }
    }

    private fun extinguishNearbyFires(): Int {
        val extinguishedFires: List<AbstractFireGrenadeEntity>
        val smokeRadius = ModConfig.SmokeGrenade.SMOKE_RADIUS.get()
        val smokeFallingHeight = ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get()
        if (this.entityData.get(isActivatedAccessor)) {
            val bb = AABB(this.blockPosition()).inflate(
                smokeRadius.toDouble(),
                smokeFallingHeight.toDouble(),
                smokeRadius.toDouble(),
            )

            extinguishedFires = this.level().getEntitiesOfClass(
                AbstractFireGrenadeEntity::class.java,
                bb,
            ) {
                it.entityData.get(isActivatedAccessor) && canDistinguishFire(it.position())
            }
        } else {
            val bb = AABB(this.blockPosition()).inflate(ModConfig.FireGrenade.FIRE_RANGE.get().toDouble())
            extinguishedFires = this.level().getEntitiesOfClass(
                AbstractFireGrenadeEntity::class.java,
                bb,
            ) {
                it.entityData.get(isActivatedAccessor) && it.getSpreadBlocks()
                    .any { pos -> pos.above().center.distanceToSqr(this.position()) < 2 }
            }
        }

        if (this.level() is ServerLevel) {
            extinguishedFires.forEach {
                it.extinguish()
            }
        }
        return extinguishedFires.size
    }

    override fun getHitDamageSource(hitEntity: LivingEntity): DamageSource {
        val registryAccess = this.level().registryAccess()
        val damageTypeHolder = registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.SMOKEGRENADE_HIT)
        return if (hitEntity == this.owner) {
            DamageSource(damageTypeHolder, this)
        } else {
            DamageSource(damageTypeHolder, this, this.owner)
        }
    }

    fun canDistinguishFire(position: Vec3): Boolean = false

    fun getSmokeDataPoints(): Set<SmokeDataPoint> = this.entityData.get(smokeDataPointsAccessor)
}
