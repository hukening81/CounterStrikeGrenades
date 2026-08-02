package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelMap
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraftforge.entity.IEntityAdditionalSpawnData
import net.minecraftforge.network.NetworkHooks
import java.util.*

class SmokeRegionEntity(entityType: EntityType<SmokeRegionEntity>, level: Level) : Entity(entityType, level),
    IEntityAdditionalSpawnData {

    var hasInitialized = false

    lateinit var ownerUUID: UUID

    lateinit var voxelMap: VoxelMap

    var smokeID: Int = 0
    var variant = SmokeGrenadeVariant.T

    init {
        noPhysics = true
    }

    override fun defineSynchedData() {
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag) {
    }

    override fun getAddEntityPacket(): Packet<ClientGamePacketListener> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun writeSpawnData(buffer: FriendlyByteBuf) {
        val data = SpawnData(
            this.ownerUUID, this.variant, this.voxelMap
        )
        buffer.writeByteArray(ProtoBuf.encodeToByteArray(SpawnData.serializer(), data))
    }


    @OptIn(ExperimentalSerializationApi::class)
    override fun readSpawnData(additionalData: FriendlyByteBuf) {
        val data = ProtoBuf.decodeFromByteArray(SpawnData.serializer(), additionalData.readByteArray())
        this.ownerUUID = data.ownerUUID
        this.variant = data.variant
        this.voxelMap = data.voxelMap
        this.boundingBox = voxelMap.boundingBox
        this.hasInitialized = true
    }

    override fun shouldBeSaved(): Boolean = false
    override fun makeBoundingBox(): AABB {
        return if (!this.hasInitialized) {
            super.makeBoundingBox()
        } else {
            this.voxelMap.boundingBox
        }
    }

    @Serializable
    data class SpawnData(
        @Serializable(with = UUIDSerializer::class) val ownerUUID: UUID,
        val variant: SmokeGrenadeVariant,
        val voxelMap: VoxelMap
    )
}