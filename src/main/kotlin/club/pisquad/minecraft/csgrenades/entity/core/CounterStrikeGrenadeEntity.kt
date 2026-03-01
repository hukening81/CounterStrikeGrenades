package club.pisquad.minecraft.csgrenades.entity.core

import club.pisquad.minecraft.csgrenades.entity.core.trajectory.CustomTrajectoryEntity
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.event.GrenadeActivateEvent
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
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

    abstract val sounds: GrenadeEntitySoundEvents
    abstract val damageTypes: GrenadeEntityDamageTypes

    val owner: Player?
        get() {
            return level().players().find { it.uuid == this.ownerUuid }
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
        super.tick()
//        TrajectoryHelper.step(level(), trajectory)
//        this.moveTo(trajectory.position.minusGrenadeSizeOffset())
    }

    override fun onAddedToWorld() {
        super.onAddedToWorld()
    }

    override fun isOnFire(): Boolean = false

    override fun shouldBeSaved(): Boolean = false

//    abstract fun getHitDamageSource(hitEntity: LivingEntity): DamageSource

    open fun activate() {
        this.entityData.set(isActivatedAccessor, true)
        if (this.level().isClientSide) {
            // EMPTY
        } else {
//            println("Firing grenade activate event ${this.grenadeType}")
            MinecraftForge.EVENT_BUS.post(GrenadeActivateEvent(this, this.grenadeType))
        }
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

    override fun getAddEntityPacket(): Packet<ClientGamePacketListener> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag?) {

    }

    override fun readAdditionalSaveData(pCompound: CompoundTag?) {

    }

    override fun onHitBlock(position: Vec3, direction: Direction) {
//        if (this.level().isClientSide) {
//            println("Client Bounce")
//            playClientBounceSound()
//        } else {
//            println("Server Bounce")
//        }
    }

    override fun onHitEntity(position: Vec3, direction: Direction, entity: Entity) {
        if (this.level().isClientSide) {
            playClientHitEntitySound()
        }
    }

    fun playClientBounceSound() {

    }

    fun playClientHitEntitySound() {

    }


    fun getDamageSource(entity: Entity, resourceKey: ResourceKey<DamageType>): DamageSource {
        val registry = entity.level().registryAccess().registry(Registries.DAMAGE_TYPE).get()
        val damageType = registry.get(resourceKey)!!
        return DamageSource(Holder.direct(damageType), this, this.owner, this.center)
    }
}
