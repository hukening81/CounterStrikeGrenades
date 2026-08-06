package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.grenades.firegrenade.flame.FlameMap
import club.pisquad.minecraft.csgrenades.runOnClient
import club.pisquad.minecraft.csgrenades.runOnServer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraftforge.entity.IEntityAdditionalSpawnData
import net.minecraftforge.network.NetworkHooks


class FireRegionEntity(entityType: EntityType<FireRegionEntity>, level: Level) : Entity(entityType, level),
    IEntityAdditionalSpawnData {
    var hasInitialized: Boolean = false
    var debugMode: Boolean = false
    lateinit var variant: FireGrenadeVariant
    lateinit var flameMap: FlameMap

    init {
        noPhysics = true
    }

    companion object {
        val clientTrackedEntities: MutableSet<FireRegionEntity> = mutableSetOf()
        val serverTrackedEntities: MutableSet<FireRegionEntity> = mutableSetOf()

        fun create(
            level: ServerLevel,
            center: Vec3,
            variant: FireGrenadeVariant,
            flameMap: FlameMap,
            debugMode: Boolean = false
        ): FireRegionEntity? {
            val entity = FireGrenadeRegistryHelper.fireRegionEntity.get().create(level)?:return null
            entity.variant = variant
            entity.flameMap = flameMap
            entity.setPos(center)
            entity.debugMode = debugMode
            entity.boundingBox = flameMap.boundingBox
            entity.hasInitialized = true

            if (level.addFreshEntity(entity)) {
                return entity
            } else {
                return null
            }
        }
    }

    override fun shouldBeSaved(): Boolean = false

    override fun defineSynchedData() {
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag) {
    }

    override fun onAddedToWorld() {
        super.onAddedToWorld()
        this.runOnServer {
            FireRegionEntity.serverTrackedEntities.add(this)
        }
        this.runOnClient {
            FireRegionEntity.clientTrackedEntities.add(this)
        }
    }

    override fun onRemovedFromWorld() {
        super.onRemovedFromWorld()
        this.runOnServer {
            FireRegionEntity.serverTrackedEntities.remove(this)
        }
        this.runOnClient {
            FireRegionEntity.clientTrackedEntities.remove(this)
        }
    }

    override fun makeBoundingBox(): AABB {
        if (hasInitialized) {
            return this.flameMap.boundingBox
        } else {
            return super.makeBoundingBox()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun writeSpawnData(buffer: FriendlyByteBuf) {
        require(this.hasInitialized)
        val data = SpawnData(this.variant, this.flameMap, this.debugMode)
        buffer.writeByteArray(ProtoBuf.encodeToByteArray(SpawnData.serializer(), data))
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun readSpawnData(additionalData: FriendlyByteBuf) {
        val data = ProtoBuf.decodeFromByteArray(SpawnData.serializer(), additionalData.readByteArray())
        this.variant = data.variant
        this.flameMap = data.flameMap
        this.debugMode = data.debugMode
        this.boundingBox = flameMap.boundingBox
        this.hasInitialized = true
    }

    override fun getAddEntityPacket(): Packet<ClientGamePacketListener> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    @Serializable
    class SpawnData(
        val variant: FireGrenadeVariant,
        val flameMap: FlameMap,
        val debugMode: Boolean
    )
}
