package club.pisquad.minecraft.csgrenades.core.entity

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.ModSettings
import club.pisquad.minecraft.csgrenades.WithGrenadeType
import club.pisquad.minecraft.csgrenades.api.CSGrenadeServerAPI
import club.pisquad.minecraft.csgrenades.api.event.GrenadeActivatedEvent
import club.pisquad.minecraft.csgrenades.api.event.GrenadeHitBlockEvent
import club.pisquad.minecraft.csgrenades.api.event.GrenadeHitEntityEvent
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonSounds
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.network.message.ServerGrenadeHitBlockMessage
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.physics.GrenadeHitSomething.GrenadeHitBlock
import club.pisquad.minecraft.csgrenades.physics.GrenadeHitSomething.GrenadeHitEntity
import club.pisquad.minecraft.csgrenades.physics.GrenadePosition
import club.pisquad.minecraft.csgrenades.physics.GrenadeVelocity
import club.pisquad.minecraft.csgrenades.physics.MovementPredictor
import club.pisquad.minecraft.csgrenades.runOnClient
import club.pisquad.minecraft.csgrenades.runOnServer
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
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
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
    val sounds: GrenadeCommonSounds
    val damageTypes: GrenadeCommonDamageTypes
}

private interface GrenadeMovement {
    var grenadePosition: GrenadePosition
    var grenadeVelocity: GrenadeVelocity
}

abstract class CounterStrikeGrenadeEntity(
    pEntityType: EntityType<out CounterStrikeGrenadeEntity>,
    pLevel: Level,
) : Entity(pEntityType, pLevel), GrenadeEntityData, WithGrenadeType, IEntityAdditionalSpawnData {

    lateinit var ownerUuid: UUID

    val rotation: GrenadeRotation

    val owner: Player?
        get() {
            return level().players().find { it.uuid == this.ownerUuid }
        }

    init {
        isNoGravity = true
        rotation = GrenadeRotation(this.id.toLong())
    }

    companion object {
        val isActivatedAccessor: EntityDataAccessor<Boolean> =
            SynchedEntityData.defineId(CounterStrikeGrenadeEntity::class.java, EntityDataSerializers.BOOLEAN)
        val isStoppedAccessor: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            CounterStrikeGrenadeEntity::class.java, EntityDataSerializers.BOOLEAN
        )

        val clientTrackedEntities: MutableSet<CounterStrikeGrenadeEntity> = mutableSetOf()
        val serverTrackedEntities: MutableMap<ResourceKey<Level>, MutableSet<CounterStrikeGrenadeEntity>> =
            mutableMapOf()
    }

    var isActivated: Boolean
        get() {
            return this.entityData.get(isActivatedAccessor)
        }
        set(value) {
            this.entityData.set(isActivatedAccessor, value)
        }
    var isStopped: Boolean
        get() {
            return this.entityData.get(isStoppedAccessor)
        }
        set(value) {
            this.entityData.set(isStoppedAccessor, value)
        }
    var center: Vec3
        get() {
            return GrenadePosition.fromWorldPos(this.position()).center
        }
        set(value) {
            this.setPos(GrenadePosition.fromCenter(value).worldPos)
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
            if (this.isStopped) {
                return@runOnServer
            }
            val movementPredict = MovementPredictor.predict(
                this.level(), GrenadePosition.fromWorldPos(this.position()),
                GrenadeVelocity.fromBlocksPerTick(this.deltaMovement)
            )
            when (movementPredict) {
                MovementPredictor.PredictResult.Error.MAX_SUBTICK_REACHED -> {
                    ModLogger.error(this) {
                        "failed to predict movement: MAX_SUBTICK_REACHED" +
                                " server discarding entity"
                    }
                    this.discard()
                }

                is MovementPredictor.PredictResult.PredictSuccess -> {
                    movementPredict.hits.forEach {
                        when (it) {
                            is GrenadeHitBlock -> {
                                val handleResult = this.onHitBlock(it)
                                val message = ServerGrenadeHitBlockMessage.create(this, it, handleResult)
                                ModPacketHandler.sendMessageToPlayer(this.level() as ServerLevel, this.center, message)
                                val event = GrenadeHitBlockEvent.create(LogicalSide.SERVER, this, it, handleResult)
                                MinecraftForge.EVENT_BUS.post(event)

                                this.deltaMovement = it.velocity.blocksPerTick
                                this.center = it.hitPoint

                                if (handleResult.shouldStop) {
                                    this.isStopped = true
                                }
                            }

                            is GrenadeHitEntity -> {
                                val handleResult = this.onHitEntity(it)
                                if (handleResult.damageAmount > 0 && it.entity is LivingEntity) {
                                    CSGrenadeServerAPI.dealHitDamage(this, it.entity, handleResult.damageAmount)
                                }
                                val event = GrenadeHitEntityEvent.create(LogicalSide.SERVER, this, it, handleResult)
                                MinecraftForge.EVENT_BUS.post(event)

                                this.deltaMovement = it.velocity.blocksPerTick
                                this.center = it.hitPoint
                            }
                        }
                    }

                    this.setPos(movementPredict.position.worldPos)
                    this.deltaMovement = movementPredict.velocity.blocksPerTick

                    movementPredict.hits.filter { it is GrenadeHitBlock }.lastOrNull()?.let {
                        require(it is GrenadeHitBlock)
                        // Predict if the grenade has stopped
                        if (it.direction == Direction.UP && this.deltaMovement.y.absoluteValue < 0.05) {
                            // Snap to the ground
                            val center = this.center
                            this.center =
                                Vec3(
                                    center.x,
                                    it.hitPoint.y + ModSettings.Entity.GRENADE_ENTITY_SIZE_HALF,
                                    center.z,
                                    )
                            this.deltaMovement = Vec3.ZERO
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
        this.runOnServer {
            CounterStrikeGrenadeEntity.serverTrackedEntities.getOrPut(this.level().dimension()) { mutableSetOf() }
                .add(this)
        }
        this.runOnClient {
            CounterStrikeGrenadeEntity.clientTrackedEntities.add(this)
        }
    }

    override fun onRemovedFromWorld() {
        super.onRemovedFromWorld()
        this.runOnServer {
            CounterStrikeGrenadeEntity.serverTrackedEntities.get(this.level().dimension())?.run {
                this.remove(this@CounterStrikeGrenadeEntity)
            }
        }
        this.runOnClient {
            CounterStrikeGrenadeEntity.clientTrackedEntities.remove(this)
        }
    }

    override fun canChangeDimensions(): Boolean = false

    override fun isOnFire(): Boolean = false

    override fun shouldBeSaved(): Boolean = false

    open fun createActivatedEvent(side: LogicalSide): GrenadeActivatedEvent {
        return GrenadeActivatedEvent(side, this.grenadeType, this.ownerUuid, this.center)
    }

    open fun activate() {
        this.runOnServer {
            this.isActivated = true
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

    open fun onHitBlock(data: GrenadeHitBlock): HitBlockHandleResult {
        ModLogger.info(this) { "Grenade hit block at ${data.hitPoint} (${data.direction})" }
        return HitBlockHandleResult()
    }

    open fun onHitEntity(data: GrenadeHitEntity): HitEntityHandleResult {
        ModLogger.info(this) { "Grenade hit entity(${data.entity}) at ${data.hitPoint} (${data.direction})" }
        return HitEntityHandleResult()
    }

    open fun onStopped() {
        this.runOnClient {
            this.rotation.makeFlat()
        }
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



@Serializable
data class HitBlockHandleResult(
    var shouldStop: Boolean = false,
    var shouldPlaySound: Boolean = true
)

@Serializable
data class HitEntityHandleResult(
    val damageAmount: Double = 1.0,
    val shouldPlaySound: Boolean = true
)