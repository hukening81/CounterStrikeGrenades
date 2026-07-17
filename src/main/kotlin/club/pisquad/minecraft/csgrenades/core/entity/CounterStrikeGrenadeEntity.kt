package club.pisquad.minecraft.csgrenades.core.entity

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.WithGrenadeType
import club.pisquad.minecraft.csgrenades.api.CSGrenadeServerAPI
import club.pisquad.minecraft.csgrenades.api.event.GrenadeActivatedEvent
import club.pisquad.minecraft.csgrenades.api.event.GrenadeHitBlockEvent
import club.pisquad.minecraft.csgrenades.api.event.GrenadeHitEntityEvent
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.network.message.ServerGrenadeHitBlockMessage
import club.pisquad.minecraft.csgrenades.network.message.ServerGrenadeHitEntityMessage
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.physics.*
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.entity.IEntityAdditionalSpawnData
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.network.NetworkHooks
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.pow

interface GrenadeEntityData {
    val sounds: GrenadeSoundEvents
    val damageTypes: GrenadeEntityDamageTypes
}

private interface GrenadeMovement {
    var grenadePosition: GrenadePosition
    var grenadeVelocity: GrenadeVelocity
}

abstract class CounterStrikeGrenadeEntity(
    pEntityType: EntityType<out CounterStrikeGrenadeEntity>,
    pLevel: Level,
) : Entity(pEntityType, pLevel), GrenadeEntityData, WithGrenadeType, GrenadeMovement, IEntityAdditionalSpawnData {

    lateinit var ownerUuid: UUID

    val rotation: GrenadeRotation

    override var grenadePosition: GrenadePosition = GrenadePosition.ZERO
        set(value) {
            this.move(
                MoverType.SELF,
                value.worldPos.subtract(field.worldPos)
            )
            field = value
        }
    override var grenadeVelocity: GrenadeVelocity = GrenadeVelocity.ZERO
        set(value) {
            this.deltaMovement = value.blocksPerTick
            field = value
        }

    val owner: Player?
        get() {
            return level().players().find { it.uuid == this.ownerUuid }
        }

    init {
        isNoGravity = true
//        noPhysics = true
        rotation = GrenadeRotation(this.id.toLong())
    }

    companion object {
        val isActivatedAccessor: EntityDataAccessor<Boolean> =
            SynchedEntityData.defineId(CounterStrikeGrenadeEntity::class.java, EntityDataSerializers.BOOLEAN)
        val isStoppedAccessor: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            CounterStrikeGrenadeEntity::class.java, EntityDataSerializers.BOOLEAN
        )
    }

    val isActivated: Boolean
        get() {
            return this.entityData.get(isActivatedAccessor)
        }
    val isStopped: Boolean
        get() {
            return this.entityData.get(isStoppedAccessor)
        }
    val center: Vec3
        get() {
            return this.grenadePosition.center
        }

    override fun defineSynchedData() {
        this.entityData.define(isActivatedAccessor, false)
        this.entityData.define(isStoppedAccessor, false)
    }

    override fun tick() {
        super.tick()

        if (this.isStopped) {
            return
        }
        this.rotation.tick()
        this.runOnServer {
            val movementPredict = MovementPredictor.predict(this.level(), this.grenadePosition, this.grenadeVelocity)
            when (movementPredict) {
                MovementPredictor.PredictResult.Error.MAX_SUBTICK_REACHED -> {
                    ModLogger.error(this) {
                        "failed to predict movement: MAX_SUBTICK_REACHED" +
                                " server discarding entity"
                    }
                    this.discard()
                }

                is MovementPredictor.PredictResult.PredictSuccess -> {
                    movementPredict.entityHits.forEach {
                        this.onHitEntity(it)
                    }
                    movementPredict.blockHits.forEach {
                        this.onHitBlock(it)
                    }
                    this.grenadePosition = movementPredict.position
                    this.grenadeVelocity = movementPredict.velocity

                    movementPredict.blockHits.lastOrNull()?.let {
                        // Predict if the grenade has stopped
                        if (it.direction == Direction.UP && this.grenadeVelocity.metersPerSecond.y.absoluteValue < 0.01) {
                            // Snap to the ground
                            this.grenadePosition = GrenadePosition.fromCenter(
                                Vec3(
                                    this.grenadePosition.center.x,
                                    it.hitPoint.y,
                                    this.grenadePosition.center.x,
                                )
                            )
                            this.entityData.set(isStoppedAccessor, true)
                            this.onStopped()
                        }
                    }
                }
            }
        }
    }

    override fun onAddedToWorld() {
        super.onAddedToWorld()
        this.grenadePosition = GrenadePosition.fromWorldPos(this.position())
        this.grenadeVelocity = GrenadeVelocity.fromBlocksPerTick(this.deltaMovement)
        CSGrenadeServerAPI.entity.register(this)
    }

    override fun onRemovedFromWorld() {
        CSGrenadeServerAPI.entity.unregister(this.uuid)
        super.onRemovedFromWorld()
    }

    override fun isOnFire(): Boolean = false

    override fun shouldBeSaved(): Boolean = false

    open fun createActivatedEvent(side: LogicalSide): GrenadeActivatedEvent {
        return GrenadeActivatedEvent(side, this.grenadeType, this.ownerUuid)
    }

    open fun activate() {
        this.runOnServer {
            this.entityData.set(isActivatedAccessor, true)
            ModLogger.debug(this) { "Firing GrenadeActivateEvent" }
            MinecraftForge.EVENT_BUS.post(this.createActivatedEvent(LogicalSide.SERVER))
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun readSpawnData(additionalData: FriendlyByteBuf) {
//        super.readSpawnData(additionalData)
        val spawnData = ProtoBuf.decodeFromByteArray(SpawnData.serializer(), additionalData.readByteArray())
        ownerUuid = spawnData.ownerUUID
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun writeSpawnData(buffer: FriendlyByteBuf) {
        val spawnData = SpawnData(ownerUuid)
        buffer.writeByteArray(ProtoBuf.encodeToByteArray(SpawnData.serializer(), spawnData))
    }

    override fun getAddEntityPacket(): Packet<ClientGamePacketListener> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag) {

    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {

    }

    override fun shouldRenderAtSqrDistance(distance: Double): Boolean {
        return distance < ModConfig.messageRange.get().pow(2)
    }

    override fun onSyncedDataUpdated(pKey: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(pKey)
        when (pKey) {
            isStoppedAccessor -> {
                this.runOnClient {
                    if (this.isStopped) {
                        this.onStopped()
                    }
                }
            }
        }
    }

    /*This function is only meant to be run on the serer */
    open fun onHitBlock(data: GrenadeHitBlock) {
        ModLogger.info(this) { "Grenade hit block at ${data.hitPoint} (${data.direction})" }

        val event = GrenadeHitBlockEvent.create(LogicalSide.SERVER, this, data)
        MinecraftForge.EVENT_BUS.post(event)

        val message = ServerGrenadeHitBlockMessage.create(this, data)
        ModPacketHandler.sendMessageToPlayer(this.level() as ServerLevel, this.center, message)
    }

    /*This function is only meant to be run on the serer */
    open fun onHitEntity(data: GrenadeHitEntity) {
        ModLogger.info(this) { "Grenade hit entity(${data.entity}) at ${data.hitPoint} (${data.direction})" }

        val event = GrenadeHitEntityEvent.create(LogicalSide.SERVER, this, data)
        MinecraftForge.EVENT_BUS.post(event)

        val message = ServerGrenadeHitEntityMessage.create(this, data)
        ModPacketHandler.sendMessageToPlayer(this.level() as ServerLevel, this.center, message)
    }


    open fun onStopped() {

    }

    override fun hashCode(): Int {
        return this.uuid.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CounterStrikeGrenadeEntity

        return this.uuid == other.uuid
    }

    @Serializable
    data class SpawnData(
        @Serializable(with = UUIDSerializer::class) val ownerUUID: UUID
    )

}

fun <T : CounterStrikeGrenadeEntity> T.runOnServer(task: T.() -> Unit) {
    if (!this.level().isClientSide) {
        task(this)
    }
}

fun <T : CounterStrikeGrenadeEntity> T.runOnClient(task: T.() -> Unit) {
    if (this.level().isClientSide) {
        task(this)
    }
}