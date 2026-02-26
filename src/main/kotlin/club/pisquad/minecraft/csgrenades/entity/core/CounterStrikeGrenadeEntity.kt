package club.pisquad.minecraft.csgrenades.entity.core

import club.pisquad.minecraft.csgrenades.entity.core.trajectory.CustomTrajectoryEntity
import club.pisquad.minecraft.csgrenades.entity.core.trajectory.TrajectoryHelper
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.event.GrenadeActivateEvent
import club.pisquad.minecraft.csgrenades.minusGrenadeSizeOffset
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
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
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.entity.IEntityAdditionalSpawnData
import net.minecraftforge.network.NetworkHooks
import java.util.*

abstract class CounterStrikeGrenadeEntity(
    pEntityType: EntityType<out CounterStrikeGrenadeEntity>,
    pLevel: Level,
    val grenadeType: GrenadeType,
) :
    CustomTrajectoryEntity(pEntityType, pLevel), IEntityAdditionalSpawnData {
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

    @Serializable
    private data class SpawnData(
        @Serializable(with = UUIDSerializer::class) val ownerUuid: UUID,
    )


    override fun defineSynchedData() {
        this.entityData.define(speedAccessor, 0f)
        this.entityData.define(isActivatedAccessor, false)
    }

    fun initialize(ownerUuid: UUID, position: Vec3, velocity: Vec3) {
        initializeMovementState(position, velocity)
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

    override fun onAddedToWorld() {
        super.onAddedToWorld()
        assert(ownerUuid.toString().isNotEmpty())

        this.playSound(this.throwSound, 0.2f, 1f)
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
        super.readSpawnData(additionalData)
        val spawnData = Cbor.decodeFromByteArray(SpawnData.serializer(), additionalData.readByteArray())
        ownerUuid = spawnData.ownerUuid
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun writeSpawnData(buffer: FriendlyByteBuf) {
        super.writeSpawnData(buffer)
        val spawnData = SpawnData(ownerUuid)
        buffer.writeByteArray(Cbor.encodeToByteArray(SpawnData.serializer(), spawnData))
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag?) {

    }

    override fun readAdditionalSaveData(pCompound: CompoundTag?) {

    }

}
