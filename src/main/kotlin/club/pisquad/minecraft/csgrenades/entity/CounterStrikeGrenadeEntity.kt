package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.config.*
import club.pisquad.minecraft.csgrenades.enums.*
import club.pisquad.minecraft.csgrenades.event.*
import club.pisquad.minecraft.csgrenades.registry.*
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance
import net.minecraft.core.Direction
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.boss.enderdragon.EndCrystal
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BarrierBlock
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.MinecraftForge
import java.util.*
import kotlin.math.*

abstract class CounterStrikeGrenadeEntity(
    pEntityType: EntityType<out ThrowableItemProjectile>,
    pLevel: Level,
    val grenadeType: GrenadeType,
) : ThrowableItemProjectile(pEntityType, pLevel),
    GrenadeEntityInterface {

    var hitBlockSound = ModSoundEvents.GRENADE_HIT.get()
    var throwSound = ModSoundEvents.GRENADE_THROW.get()
    var lastHitEntity: UUID? = null

    // For client side rotation
    private var customXRotSpeed: Float = 0f
    private var customYRotSpeed: Float = 0f
    private var customZRotSpeed: Float = 0f

    var customXRot: Float = 0f
    var customYRot: Float = 0f
    var customZRot: Float = 0f
    var customXRotO: Float = 0f
    var customYRotO: Float = 0f
    var customZRotO: Float = 0f

    var center: Vec3
        get() {
            return this.position().add(GRENADE_ENTITY_SIZE.div(2.0), GRENADE_ENTITY_SIZE.div(2.0), GRENADE_ENTITY_SIZE.div(2.0))
        }
        set(newCenter: Vec3) {
            val position = newCenter.minus(Vec3(GRENADE_ENTITY_SIZE.div(2.0), GRENADE_ENTITY_SIZE.div(2.0), GRENADE_ENTITY_SIZE.div(2.0)))
            this.setPos(position)
        }

    init {
        if (pLevel.isClientSide) {
            randomizeRotation()
        }
    }

    companion object {
        val speedAccessor: EntityDataAccessor<Float> =
            SynchedEntityData.defineId(CounterStrikeGrenadeEntity::class.java, EntityDataSerializers.FLOAT)
        val isActivatedAccessor: EntityDataAccessor<Boolean> =
            SynchedEntityData.defineId(CounterStrikeGrenadeEntity::class.java, EntityDataSerializers.BOOLEAN)

//        fun registerGrenadeEntityEventHandler(bus: IEventBus) {
//            HEGrenadeEntity.registerEventHandler(bus)
//        }
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        this.entityData.define(speedAccessor, 0f)
//        this.entityData.define(isLandedAccessor, false)
//        this.entityData.define(isExplodedAccessor, false)
        this.entityData.define(isActivatedAccessor, false)
    }

    fun isActivated(): Boolean = this.entityData.get(isActivatedAccessor)

    override fun onHitEntity(result: EntityHitResult) {
        if (level().isClientSide) {
            randomizeRotation()
        }

        if (result.entity is EndCrystal) {
            result.entity.hurt(result.entity.damageSources().generic(), 1f)
        }

        if (result.entity is LivingEntity) {
            val entity = result.entity as LivingEntity

            val originalKnockBackResistance =
                entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE)?.baseValue ?: 0.0

            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE)?.baseValue = 1.0
            entity.hurt(this.getHitDamageSource(entity), 1f)

            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE)?.baseValue = originalKnockBackResistance
        }
        if (this.lastHitEntity == null || this.lastHitEntity != result.entity.uuid) {
            val direction: Direction = this.deltaMovement.snapToAxis().opposite
            this.bounce(direction, 0.1f, 0.08f)
            this.lastHitEntity = result.entity.uuid
        }
    }

    override fun tick() {
        super.tick()

        // New, more robust landing detection
        if (!this.entityData.get(isActivatedAccessor)) {
            if (this.onGround() && this.deltaMovement.lengthSqr() < 0.01 * 0.01) {
//                this.entityData.set(isLandedAccessor, true)
                this.activate()
            }
        }

        // Client-side rotation logic
        if (this.level().isClientSide) {
            if (!this.entityData.get(isActivatedAccessor)) {
                // In air: keep rotating
                this.customXRotO = this.customXRot
                this.customYRotO = this.customYRot
                this.customZRotO = this.customZRot

                this.customXRot = (this.customXRot + customXRotSpeed) % 360
                this.customYRot = (this.customYRot + customYRotSpeed) % 360
                this.customZRot = (this.customZRot + customZRotSpeed) % 360

                // Air resistance
                this.customXRotSpeed *= 0.99f
                this.customYRotSpeed *= 0.99f
                this.customZRotSpeed *= 0.99f
            } else {
                // On ground: stop rotation
                this.customXRotSpeed = 0f
                this.customYRotSpeed = 0f
                this.customZRotSpeed = 0f
            }
        }
    }

    /**
     * Called when the entity is added to the world.
     *
     * This function plays the sound effect of a grenade being thrown when the entity is added to the world.
     *
     * @return None
     */
    override fun onAddedToWorld() {
        this.playSound(this.throwSound, 0.2f, 1f)
    }

    /**
     * Handles the bounce logic for custom grenades when they hit a block.
     *
     * This function updates the entity's movement and position based on the block hit result,
     * simulating a bouncing effect. It also plays a sound effect when the entity hits a block on the client side.
     *
     * @param result The block hit result.
     */
    override fun onHitBlock(result: BlockHitResult) {
        if (this.entityData.get(isActivatedAccessor)) {
            return
        }
        if (ModConfig.IGNORE_BARRIER_BLOCK.get() && this.level()
                .getBlockState(result.blockPos).block is BarrierBlock
        ) {
            return
        }

        if (level().isClientSide) {
            randomizeRotation()
            if (!this.entityData.get(isActivatedAccessor)) {
                val player = Minecraft.getInstance().player!!
                val distance = this.position().add(player.position().reverse()).length()
                val soundInstance = EntityBoundSoundInstance(
                    hitBlockSound,
                    SoundSource.AMBIENT,
                    SoundUtils.getVolumeFromDistance(
                        distance,
                        SoundTypes.GRENADE_HIT, // unify volume for all grenades hit sounds
                    ).toFloat(),
                    1f,
                    this,
                    0,
                )
                Minecraft.getInstance().soundManager.play(soundInstance)
            }
        }

        this.handleBounce(result)
        // fix: the entity will keep bouncing on the ground
        if (result.direction == Direction.UP && this.deltaMovement.length() < 0.05) {
            //            this.setPos(this.x, result.blockPos.y.toDouble() + 1, this.z)
            this.deltaMovement = Vec3.ZERO
            this.isNoGravity = true
        }
    }

    private fun randomizeRotation() {
        this.customXRotSpeed = (random.nextFloat() - 0.5f) * 40
        this.customYRotSpeed = (random.nextFloat() - 0.5f) * 40
        this.customZRotSpeed = (random.nextFloat() - 0.5f) * 40
    }

    private fun handleBounce(result: BlockHitResult) {
        // Get intersect point

        val relativePos = this.center.minus(result.blockPos.center)
        val speed = this.deltaMovement
        var scale: Double = 0.0
        val collisionPoint = when (result.direction) {
            Direction.DOWN, Direction.UP -> {
                scale = (relativePos.y.absoluteValue - 0.5 - GRENADE_ENTITY_SIZE_HALF).div(this.deltaMovement.y.absoluteValue)
                this.center.add(Vec3(0.0, GRENADE_ENTITY_SIZE_HALF.times(relativePos.y.sign), 0.0)).add(this.deltaMovement.scale(scale))
            }

            Direction.NORTH, Direction.SOUTH -> {
                scale = (relativePos.z.absoluteValue - 0.5 - GRENADE_ENTITY_SIZE_HALF).div(this.deltaMovement.z.absoluteValue)
                this.center.add(Vec3(0.0, 0.0, GRENADE_ENTITY_SIZE_HALF.times(relativePos.y.sign))).add(this.deltaMovement.scale(scale))
            }

            Direction.WEST, Direction.EAST -> {
                scale = (relativePos.x.absoluteValue - 0.5 - GRENADE_ENTITY_SIZE_HALF).div(this.deltaMovement.x.absoluteValue)
                this.center.add(Vec3(GRENADE_ENTITY_SIZE_HALF.times(relativePos.y.sign), 0.0, 0.0)).add(this.deltaMovement.scale(scale))
            }
        }
        val newSpeed = when (result.direction) {
            Direction.UP, Direction.DOWN -> {
                Vec3(speed.x.times(1 - BOUNCE_FRICTION), speed.y.times(-1.0).times(BOUNCE_RESTORATION_RATE), speed.z.times(1 - BOUNCE_FRICTION))
            }

            Direction.NORTH, Direction.SOUTH -> {
                Vec3(speed.x.times(1 - BOUNCE_FRICTION), speed.y.times(1 - BOUNCE_FRICTION), speed.z.times(-1.0).times(BOUNCE_RESTORATION_RATE))
            }

            Direction.WEST, Direction.EAST -> {
                Vec3(speed.x.times(-1.0).times(BOUNCE_RESTORATION_RATE), speed.y.times(1 - BOUNCE_FRICTION), speed.z.times(1 - BOUNCE_FRICTION))
            }
        }
        println("newspeed $newSpeed")
        this.center = collisionPoint.add(newSpeed.scale(1 - scale))
        this.deltaMovement = newSpeed.add(Vec3(0.0, -this.gravity * (1 - scale), 0.0))
    }

    private fun bounce(direction: Direction, speedCoefficient: Float, frictionFactor: Float) {
        this.deltaMovement = when (direction) {
            Direction.UP, Direction.DOWN ->
                Vec3(
                    deltaMovement.x * frictionFactor,
                    -deltaMovement.y * speedCoefficient,
                    deltaMovement.z * frictionFactor,
                )

            Direction.WEST, Direction.EAST ->
                Vec3(
                    -deltaMovement.x * speedCoefficient,
                    deltaMovement.y * frictionFactor,
                    deltaMovement.z * frictionFactor,
                )

            Direction.NORTH, Direction.SOUTH ->
                Vec3(
                    deltaMovement.x * frictionFactor,
                    deltaMovement.y * frictionFactor,
                    -deltaMovement.z * speedCoefficient,
                )
        }
    }

    override fun isOnFire(): Boolean = false

    override fun shouldBeSaved(): Boolean = false

    abstract fun getHitDamageSource(hitEntity: LivingEntity): DamageSource

    override fun activate() {
        this.deltaMovement = Vec3.ZERO
        this.isNoGravity = true

        this.entityData.set(isActivatedAccessor, true)
        if (this.level().isClientSide) {
            // EMPTY
        } else {
            println("Firing grenade activate event ${this.grenadeType}")
            MinecraftForge.EVENT_BUS.post(GrenadeActivateEvent(this, this.grenadeType))
        }
    }
}

/**
 * Abstract class for Smoke and Decoy
 * These only activate after landing
 * @param delay Grenade will activate after this amount of delay (in tick)
 * */
abstract class ActivateAfterLandingGrenadeEntity(
    pEntityType: EntityType<out ThrowableItemProjectile>,
    pLevel: Level,
    grenadeType: GrenadeType,
    val delay: Int,
) : CounterStrikeGrenadeEntity(pEntityType, pLevel, grenadeType) {
    var tickSinceLanding: Int = 0

    companion object {
        val isLandedAccessor: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(ActivateAfterLandingGrenadeEntity::class.java, EntityDataSerializers.BOOLEAN)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        this.entityData.define(isLandedAccessor, false)
    }

    override fun tick() {
        super.tick()
        // This is a little bit tricky
        // super.onHitBlock() contains a mechanism to freeze the entity after landing on groud
        // we rely on that the check if we are landed
        if (this.level().isClientSide) {
            // EMPTY
        } else {
            if (this.entityData.get(isActivatedAccessor)) {
                // EMPTY
            } else {
                if (this.entityData.get(isLandedAccessor)) {
                    if (tickSinceLanding > delay) {
                        this.activate()
                    }
                    tickSinceLanding++
                } else {
                    this.entityData.set(isLandedAccessor, true)
                }
            }
        }
    }
}

/**
 * Abstract class for HE Grenade and Fire Grenade
 * These grenades activate after certain amount of time
 * @param fuseTime Grenade will activate after this amount of delay (in tick)
 * */
abstract class SetTimeActivateGrenadeEntity(
    pEntityType: EntityType<out ThrowableItemProjectile>,
    pLevel: Level,
    grenadeType: GrenadeType,
    val fuseTime: Int,
) : CounterStrikeGrenadeEntity(pEntityType, pLevel, grenadeType) {
    // Can we use entity.tickCount here?
    var tickSinceSpawn: Int = 0

    override fun tick() {
        super.tick()
        tickSinceSpawn++
        if (!this.entityData.get(isActivatedAccessor) && tickSinceSpawn > fuseTime) {
            this.activate()
        }
    }
}
