package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.GRENADE_ENTITY_SIZE_HALF
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.event.GrenadeActivateEvent
import club.pisquad.minecraft.csgrenades.minus
import club.pisquad.minecraft.csgrenades.minusEntityOffest
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
import club.pisquad.minecraft.csgrenades.util.trajectory.Trajectory
import club.pisquad.minecraft.csgrenades.util.trajectory.TrajectoryHelper
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.entity.IEntityAdditionalSpawnData
import net.minecraftforge.network.NetworkHooks
import java.util.*

abstract class CounterStrikeGrenadeEntity(
    pEntityType: EntityType<out ThrowableItemProjectile>,
    pLevel: Level,
    val grenadeType: GrenadeType,
) : Entity(pEntityType, pLevel),
    ICounterStrikeGrenadeEntity, IEntityAdditionalSpawnData {
    override lateinit var ownerUuid: UUID

    var hitBlockSound = ModSoundEvents.GRENADE_HIT.get()
    var throwSound = ModSoundEvents.GRENADE_THROW.get()

    var trajectory: Trajectory = Trajectory(Vec3.ZERO, Vec3.ZERO)

    override var center: Vec3
        get() {
            return this.position().add(GRENADE_ENTITY_SIZE_HALF, GRENADE_ENTITY_SIZE_HALF, GRENADE_ENTITY_SIZE_HALF)
        }
        set(newCenter: Vec3) {
            val position = newCenter.minus(
                Vec3(GRENADE_ENTITY_SIZE_HALF, GRENADE_ENTITY_SIZE_HALF, GRENADE_ENTITY_SIZE_HALF),
            )
            this.setPos(position)
        }
    var centerOld: Vec3
        get() {
            return Vec3(
                this.xo, this.yo, this.zo,
            ).add(GRENADE_ENTITY_SIZE_HALF, GRENADE_ENTITY_SIZE_HALF, GRENADE_ENTITY_SIZE_HALF)
        }
        set(newCenter) {
            this.xOld = newCenter.x.minus(GRENADE_ENTITY_SIZE_HALF)
            this.yOld = newCenter.y.minus(GRENADE_ENTITY_SIZE_HALF)
            this.zOld = newCenter.z.minus(GRENADE_ENTITY_SIZE_HALF)
            this.xo = this.xOld
            this.yo = this.yOld
            this.zo = this.zOld
        }

    // Velocity is different from deltaMovement, latter one is the displacement between ticks
    override val velocity: Vec3
        get() {
            return trajectory.velocity
        }

    init {
        isNoGravity = true
        noPhysics = true
    }

    companion object {
        val speedAccessor: EntityDataAccessor<Float> =
            SynchedEntityData.defineId(CounterStrikeGrenadeEntity::class.java, EntityDataSerializers.FLOAT)
        val isActivatedAccessor: EntityDataAccessor<Boolean> =
            SynchedEntityData.defineId(CounterStrikeGrenadeEntity::class.java, EntityDataSerializers.BOOLEAN)
    }

    override fun defineSynchedData() {
        this.entityData.define(speedAccessor, 0f)
        this.entityData.define(isActivatedAccessor, false)
    }

    override fun initialize(ownerUuid: UUID, position: Vec3, velocity: Vec3) {
        initializeMovement(position,velocity)
        this.ownerUuid = ownerUuid
    }

    fun isActivated(): Boolean = this.entityData.get(isActivatedAccessor)

    override fun tick() {
//        super.tick()
        super.baseTick()
//        if (level().isClientSide) {
//            // EMPTY
//        } else {
        if (this.level().isClientSide) {
            println("current ${this.x}\t${this.y}${this.z}")
            println("old ${this.xOld}\t${this.yOld}${this.zOld}")
        }
        TrajectoryHelper.step(level(), trajectory)
        this.moveTo(trajectory.position.minusEntityOffest())
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


    private fun updateOldState() {
        this.centerOld = this.center
    }

    override fun isOnFire(): Boolean = false

    override fun shouldBeSaved(): Boolean = false

    abstract fun getHitDamageSource(hitEntity: LivingEntity): DamageSource

    open fun activate() {
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

    override fun getAddEntityPacket(): Packet<ClientGamePacketListener> {
        // This still calls the methods below automatically
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun readSpawnData(additionalData: FriendlyByteBuf) {
        val data = Cbor.decodeFromByteArray(GrenadeEntitySpawnData.serializer(),additionalData.readByteArray())
        initializeMovement(data.position,data.velocity)
        this.ownerUuid = data.ownerUuid
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun writeSpawnData(buffer: FriendlyByteBuf) {
        val firstNode = this.trajectory.nodes[0]
        val data = GrenadeEntitySpawnData(
            this.ownerUuid,
            firstNode.position,
            firstNode.velocity
        )
        val byteArray = Cbor.encodeToByteArray(GrenadeEntitySpawnData.serializer(), data)
        buffer.writeByteArray(byteArray)
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag) {
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {
    }
   /**Should be called before adding to the world
    * */
    private fun initializeMovement(position: Vec3,velocity:Vec3){
       this.trajectory.replaceNode(0, Trajectory.TrajectoryNode(0, position, velocity, 0.0))
    }
}

@Serializable
private data class GrenadeEntitySpawnData(
    @Serializable(with = UUIDSerializer::class) val ownerUuid: UUID,
    @Serializable(with= Vec3Serializer::class) val position:Vec3,
    @Serializable(with= Vec3Serializer::class) val velocity:Vec3,
)


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
                    // super.onHitBlock() contains a mechanism to freeze the entity after landing on groud
                    // we rely on that the check if we are landed
                    if (this.deltaMovement == Vec3.ZERO) {
                        this.entityData.set(isLandedAccessor, true)
                    }
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
