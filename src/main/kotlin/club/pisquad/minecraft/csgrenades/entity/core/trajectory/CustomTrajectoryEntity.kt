package club.pisquad.minecraft.csgrenades.entity.core.trajectory

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GRENADE_ENTITY_SIZE_HALF
import club.pisquad.minecraft.csgrenades.addGrenadeSizeOffset
import club.pisquad.minecraft.csgrenades.minusGrenadeSizeOffset
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.entity.IEntityAdditionalSpawnData
import net.minecraftforge.network.NetworkHooks

/**Helper class that implements custom physics required by grenades
 * It updates vanilla `position` and `deltaMovement`, alongside the provided `center` and `velocity`
 * */
abstract class CustomTrajectoryEntity(
    pEntityType: EntityType<out CustomTrajectoryEntity>,
    pLevel: Level,
) : Entity(pEntityType, pLevel), IEntityAdditionalSpawnData {

    var center: Vec3
        get() {
            return this.position().addGrenadeSizeOffset()
        }
        set(newCenter) {
            val position = newCenter.minusGrenadeSizeOffset()
            this.setPos(position)
        }
    var centerOld: Vec3
        get() {
            return Vec3(
                this.xo, this.yo, this.zo,
            ).add(GRENADE_ENTITY_SIZE_HALF, GRENADE_ENTITY_SIZE_HALF, GRENADE_ENTITY_SIZE_HALF)
        }
        set(newCenter) {
            val offset = newCenter.minusGrenadeSizeOffset()
            this.xOld = offset.x
            this.yOld = offset.y
            this.zOld = offset.z
            this.xo = offset.x
            this.yo = offset.y
            this.zo = offset.z
        }

    // Velocity is different from deltaMovement, latter one is the displacement between ticks
    val velocity: Vec3
        get() {
            return trajectory.velocity
        }

    var trajectory: Trajectory = Trajectory()

    @Serializable
    private data class SpawnData(
        @Serializable(with = Vec3Serializer::class) val position: Vec3,
        @Serializable(with = Vec3Serializer::class) val velocity: Vec3,
    )

    companion object {
        val trajectoryNodeUpdateAccessor: EntityDataAccessor<TrajectoryNode.TickNode> =
            SynchedEntityData.defineId(CustomTrajectoryEntity::class.java, TrajectoryNode.TickNode.TickNodeEntityDataSerializer())
    }

    override fun defineSynchedData() {
        this.entityData.define(trajectoryNodeUpdateAccessor, TrajectoryNode.TickNode.empty())
    }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)
        if (this.level().isClientSide) {
            if (key == trajectoryNodeUpdateAccessor) {
                val serverNode = this.entityData.get(key) as TrajectoryNode.TickNode
                if (serverNode.position == Vec3.ZERO && serverNode.velocity == Vec3.ZERO) {
                    println("Somehow the serve sent a empty node?")
                } else {
                    trajectory.syncServerNode(serverNode, this.level() as ClientLevel)
                }
            }
        }
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag?) {

    }

    override fun addAdditionalSaveData(pCompound: CompoundTag?) {

    }


    @OptIn(ExperimentalSerializationApi::class)
    override fun readSpawnData(additionalData: FriendlyByteBuf) {
        val spawnData: SpawnData = Cbor.decodeFromByteArray(SpawnData.serializer(), additionalData.readByteArray())
        initializeMovementState(spawnData.position, spawnData.velocity)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun writeSpawnData(buffer: FriendlyByteBuf) {
        val spawnData = SpawnData(
            this.center,
            this.velocity,
        )
        buffer.writeByteArray(Cbor.encodeToByteArray(SpawnData.serializer(), spawnData))
    }

    override fun getAddEntityPacket(): Packet<ClientGamePacketListener> {
        // Ensures additional spawn data is properly handled
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    fun initializeMovementState(position: Vec3, velocity: Vec3) {
        trajectory.initialize(position, velocity)
    }

    override fun tick() {
        val node = this.trajectory.tick(this.level())
        if (this.level().isClientSide) {
            if (trajectory.initialized) {
                CounterStrikeGrenades.Logger.warn("Client trajectory not initialized")
            }
        } else {
            this.entityData.set(trajectoryNodeUpdateAccessor, node)
        }
    }


    /**Provide basic hook for playing sound events
     * */
    abstract fun onHitBlock()

    abstract fun onHitEntity(entity: Entity)
}
