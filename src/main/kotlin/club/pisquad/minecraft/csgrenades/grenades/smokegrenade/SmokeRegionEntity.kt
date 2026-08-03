package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages.SmokePatch
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelMap
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelPos
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.physics.GrenadeDuration
import club.pisquad.minecraft.csgrenades.runOnServer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraftforge.entity.IEntityAdditionalSpawnData
import net.minecraftforge.network.NetworkHooks
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Clock

private const val PARTICLE_COUNT_PER_VOXEL = 3

class SmokeRegionEntity(entityType: EntityType<SmokeRegionEntity>, level: Level) : Entity(entityType, level),
    IEntityAdditionalSpawnData {

    var hasInitialized = false

    lateinit var ownerUUID: UUID

    lateinit var voxelMap: VoxelMap

    var smokeID: Int = 0
    var variant = SmokeGrenadeVariant.T
    var randomSeed = Random.nextLong()

    var activateTime: Long = 0

    val trackedParticles: MutableSet<SmokeGrenadeParticle> = mutableSetOf()

    init {
        noPhysics = true
    }

    companion object {
        // Only supported on server
        val trackedRegions: MutableSet<SmokeRegionEntity> = mutableSetOf()
    }

    override fun onAddedToWorld() {
        super.onAddedToWorld()
        this.runOnServer {
            SmokeRegionEntity.trackedRegions.add(this)
        }
    }

    override fun onRemovedFromWorld() {
        super.onRemovedFromWorld()
        this.runOnServer {
            SmokeRegionEntity.trackedRegions.remove(this)
        }
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
        this.activateTime = Clock.System.now().toEpochMilliseconds()
        val data = SpawnData(
            this.ownerUUID, this.variant, this.voxelMap, this.activateTime, this.randomSeed,
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
        this.activateTime = data.activateTime
        this.hasInitialized = true
        this.randomSeed = data.randomSeed

        // When `readSpawnData` is called, it means this entity is loaded/reloaded from the server.
        // We will need to respawn those particles.
        // Can't use `onAddedToWorld` because it is called before `readSpawnData` and we need additional information
        // to spawn thoese particles
        this.createClientSideParticles()
    }

    override fun shouldBeSaved(): Boolean = false
    override fun makeBoundingBox(): AABB {
        this.boundingBox = if (!this.hasInitialized) {
            super.makeBoundingBox()
        } else {
            this.voxelMap.boundingBox
        }
        return this.boundingBox
    }

    private fun createClientSideParticles() {
        val particleEngine = Minecraft.getInstance().particleEngine
        val timeSinceStart = (Clock.System.now().toEpochMilliseconds() - this.activateTime).div(1000.0)
        val randomSource = Random(this.randomSeed)
        val lifeTime =
            GrenadeDuration.fromSeconds(SmokeGrenadeConfig.duration.get() - timeSinceStart).ticks.roundToInt()

        if (lifeTime <= 0) {
            return
        }

        val getFadeInTimeFromDistance = { distance: Double ->
            0.5
        }
        val createForVoxel = { pos: VoxelPos ->
            buildSet {
                repeat(PARTICLE_COUNT_PER_VOXEL) {
                    val offset = Vec3(
                        randomSource.nextDouble(0.0, 0.5),
                        randomSource.nextDouble(0.0, 0.5),
                        randomSource.nextDouble(0.0, 0.5),
                    )
                    val position =
                        pos.toWorldPos().add(offset)
                    val particle = particleEngine.createParticle(
                        variant.particle.get(),
                        position.x,
                        position.y,
                        position.z,
                        0.0, 0.0, 0.0,
                    )
                    if (particle != null) {
                        particle.lifetime = lifeTime
                        add(particle as SmokeGrenadeParticle)
                    }
                }
            }
        }
        this.voxelMap.keys.forEach {
            this.trackedParticles.addAll(
                createForVoxel(it)
            )
        }
    }

    fun applyPatch(patch: SmokePatch) {
        patch.apply(this.trackedParticles)
    }

    @Serializable
    data class SpawnData(
        @Serializable(with = UUIDSerializer::class) val ownerUUID: UUID,
        val variant: SmokeGrenadeVariant,
        val voxelMap: VoxelMap,
        val activateTime: Long,
        val randomSeed: Long,
    )
}