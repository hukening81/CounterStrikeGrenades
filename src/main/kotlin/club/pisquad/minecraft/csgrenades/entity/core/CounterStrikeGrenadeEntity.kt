package club.pisquad.minecraft.csgrenades.entity.core

import club.pisquad.minecraft.csgrenades.entity.core.trajectory.CustomTrajectoryEntity
import club.pisquad.minecraft.csgrenades.entity.core.trajectory.Trajectory
import club.pisquad.minecraft.csgrenades.entity.core.trajectory.TrajectoryHelper
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.event.GrenadeActivateEvent
import club.pisquad.minecraft.csgrenades.minusGrenadeSizeOffset
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.network.NetworkHooks
import java.util.*

abstract class CounterStrikeGrenadeEntity(
    pEntityType: EntityType<out CounterStrikeGrenadeEntity>,
    pLevel: Level,
    val grenadeType: GrenadeType,
) :
    CustomTrajectoryEntity(pEntityType, pLevel) {
    lateinit var ownerUuid: UUID

    var hitBlockSound = ModSoundEvents.GRENADE_HIT.get()
    var throwSound = ModSoundEvents.GRENADE_THROW.get()


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

    fun initialize(ownerUuid: UUID, position: Vec3, velocity: Vec3) {
        initializeMovement(position, velocity)
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
        this.moveTo(trajectory.position.minusGrenadeSizeOffset())
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
        val data = Cbor.decodeFromByteArray(GrenadeEntitySpawnData.serializer(), additionalData.readByteArray())
        initializeMovement(data.position, data.velocity)
        this.ownerUuid = data.ownerUuid
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun writeSpawnData(buffer: FriendlyByteBuf) {
        val firstNode = this.trajectory.nodes[0]
        val data = GrenadeEntitySpawnData(
            this.ownerUuid,
            firstNode.position,
            firstNode.velocity,
        )
        val byteArray = Cbor.encodeToByteArray(GrenadeEntitySpawnData.serializer(), data)
        buffer.writeByteArray(byteArray)
    }

    /**Should be called before adding to the world
     * */
    private fun initializeMovement(position: Vec3, velocity: Vec3) {
        this.trajectory.replaceNode(0, Trajectory.TrajectoryNode(0, position, velocity, 0.0))
    }
}

@Serializable
private data class GrenadeEntitySpawnData(
    @Serializable(with = UUIDSerializer::class) val ownerUuid: UUID,
    @Serializable(with = Vec3Serializer::class) val position: Vec3,
    @Serializable(with = Vec3Serializer::class) val velocity: Vec3,
)
