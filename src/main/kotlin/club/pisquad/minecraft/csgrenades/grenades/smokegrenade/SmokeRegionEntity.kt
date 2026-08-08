package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireRegionEntity
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages.ServerSmokeDisperseMessage
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages.SmokePatch
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelMap
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelPos
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.physics.GrenadeDuration
import club.pisquad.minecraft.csgrenades.runOnClient
import club.pisquad.minecraft.csgrenades.runOnServer
import club.pisquad.minecraft.csgrenades.utils.easeOutQuart
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
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

private const val PARTICLE_COUNT_PER_VOXEL = 3
private const val SMOKE_PARTICLE_TRANSITION_TICK = 5

class SmokeRegionEntity(entityType: EntityType<SmokeRegionEntity>, level: Level) : Entity(entityType, level),
    IEntityAdditionalSpawnData {

    var hasInitialized = false

    lateinit var ownerUUID: UUID
    lateinit var voxelMap: VoxelMap

    var debugMode: VoxelDebugMode = VoxelDebugMode.NONE

    var smokeID: Int = 0
    var variant = SmokeGrenadeVariant.T
    var randomSeed = Random.nextLong()

    var lifetime: Int = 0

    val trackedParticles: MutableSet<SmokeGrenadeParticle> = mutableSetOf()

    init {
        noPhysics = true
    }

    companion object {
        val serverTrackedRegions: MutableMap<ResourceKey<Level>, MutableSet<SmokeRegionEntity>> = mutableMapOf()
        val clientTrackedRegions: MutableSet<SmokeRegionEntity> = mutableSetOf()

        fun isVoxelInSmoke(level: Level, voxelPos: VoxelPos): Boolean {
            val bb = voxelPos.boundibgBox
            return if (level.isClientSide) {
                this.clientTrackedRegions.any() {
                    it.boundingBox.intersects(bb) && it.voxelMap.keys.any() { it == voxelPos }
                }
            } else {
                this.serverTrackedRegions.get(level.dimension())?.any() {
                    it.boundingBox.intersects(bb) && it.voxelMap.keys.any() { it == voxelPos }
                }?:false
            }
        }
    }

    override fun tick() {
        super.tick()
        this.runOnServer {
            if (this.tickCount > this.lifetime) {
                this.discard()
            }
        }
    }

    override fun onAddedToWorld() {
        super.onAddedToWorld()
        this.runOnServer {
            SmokeRegionEntity.serverTrackedRegions.getOrPut(this.level().dimension()) { mutableSetOf() }.add(this)

            val shouldExtinguish = FireRegionEntity.serverTrackedEntities.get(this.level().dimension())?.filter {
                it.boundingBox.intersects(this.boundingBox)
            }?.filter {
                val totalVoxelCount = it.flameMap.keys.count()
                if (totalVoxelCount == 0) {
                    true
                }
                val coveredVoxelCount = it.flameMap.keys.filter { flameVoxel ->
                    this.voxelMap.keys.any() { it == flameVoxel }
                }.count()
                coveredVoxelCount / totalVoxelCount.toDouble() > 0.3
            }
                ?:emptyList<FireRegionEntity>()
            shouldExtinguish.forEach {
                it.extinguish()
            }
        }
        this.runOnClient {
            SmokeRegionEntity.clientTrackedRegions.add(this)
        }
    }

    override fun onRemovedFromWorld() {
        super.onRemovedFromWorld()
        this.runOnServer {
            val collection = SmokeRegionEntity.serverTrackedRegions.get(this.level().dimension())
            if (collection != null) {
                collection.remove(this)
            }
        }
        this.runOnClient {
            SmokeRegionEntity.clientTrackedRegions.remove(this)
        }
    }

    override fun canChangeDimensions(): Boolean = false

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
            this.ownerUUID, this.variant, this.voxelMap, this.randomSeed, this.debugMode, this.lifetime
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
        this.randomSeed = data.randomSeed
        this.debugMode = data.debugMode
        this.lifetime = data.lifetime
        this.hasInitialized = true

        // When `readSpawnData` is called, it means this entity is loaded/reloaded from the server.
        // We will need to respawn those particles.
        // Can't use `onAddedToWorld` because it is called before `readSpawnData` and we need additional information
        // to spawn thoese particles
        if (this.debugMode == VoxelDebugMode.NONE) {
            this.createClientSideParticles()
        }
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
        val randomSource = Random(this.randomSeed)
        val lifeTime = GrenadeDuration.fromSeconds(SmokeGrenadeConfig.duration.get()).wholeTick - this.tickCount

        if (lifeTime <= 0) {
            return
        }

        val radius = SmokeGrenadeConfig.spread.smokeWidth.get()

        val getTransitionTimeFromDistance = { distance: Double ->
            (easeOutQuart((distance/radius).coerceIn(0.0,1.0))*SMOKE_PARTICLE_TRANSITION_TICK).roundToInt()

//            Mth.lerp((distance / radius).coerceIn(0.0, 1.0), 0.0, SMOKE_PARTICLE_TRANSITION_TICK.toDouble())
//                .roundToInt()
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
                        pos.worldPos().add(offset)
                    val transitionTime =
                        getTransitionTimeFromDistance(position.distanceTo(this@SmokeRegionEntity.position()))
                    val particle = particleEngine.createParticle(
                        variant.particle.get(),
                        position.x,
                        position.y,
                        position.z,
                        0.0, 0.0, 0.0,
                    ) as SmokeGrenadeParticle?
                    if (particle != null) {
                        particle.lifetime = (lifeTime - transitionTime)
                        particle.hide(transitionTime)
                        add(particle)
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
        this.runOnServer {
            val message = ServerSmokeDisperseMessage(this.id, patch)
            ModPacketHandler.sendMessageToPlayer(this.level() as ServerLevel, this.position(), message)
        }
        this.runOnClient {
            patch.apply(this.trackedParticles)
        }
    }


    @Serializable
    data class SpawnData(
        @Serializable(with = UUIDSerializer::class) val ownerUUID: UUID,
        val variant: SmokeGrenadeVariant,
        val voxelMap: VoxelMap,
        val randomSeed: Long,
        val debugMode: VoxelDebugMode,
        val lifetime: Int,
    )

}