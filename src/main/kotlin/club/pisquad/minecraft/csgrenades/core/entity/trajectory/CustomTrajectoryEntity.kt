package club.pisquad.minecraft.csgrenades.core.entity.trajectory

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.addGrenadeSizeOffset
import club.pisquad.minecraft.csgrenades.minus
import club.pisquad.minecraft.csgrenades.minusGrenadeSizeOffset
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.network.message.ServerGrenadeMovementSyncMessage
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
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
import net.minecraft.world.phys.Vec3
import net.minecraftforge.entity.IEntityAdditionalSpawnData
import net.minecraftforge.network.NetworkHooks
import net.minecraftforge.network.PacketDistributor

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
            this.moveTo(position)
        }
    val centerOld: Vec3
        get() {
            return Vec3(
                this.xo, this.yo, this.zo,
            ).addGrenadeSizeOffset()
        }

    // Velocity is different from deltaMovement, latter one is the displacement between ticks
    val velocity: Vec3
        get() {
            return trajectory.velocity
        }

    var trajectory: Trajectory = Trajectory(
        this::onHitBlock,
        this::onHitEntity,
        this::onTrajectoryComplete,
    )

    @Serializable
    private data class SpawnData(
        @Serializable(with = Vec3Serializer::class) val position: Vec3,
        @Serializable(with = Vec3Serializer::class) val velocity: Vec3,
    )


    override fun readAdditionalSaveData(pCompound: CompoundTag) {

    }

    override fun addAdditionalSaveData(pCompound: CompoundTag) {

    }


    @OptIn(ExperimentalSerializationApi::class)
    override fun readSpawnData(additionalData: FriendlyByteBuf) {
        val spawnData: SpawnData = ProtoBuf.decodeFromByteArray(SpawnData.serializer(), additionalData.readByteArray())
        initializeMovementState(spawnData.position, spawnData.velocity)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun writeSpawnData(buffer: FriendlyByteBuf) {
        val spawnData = SpawnData(
            this.center,
            this.velocity,
        )
        buffer.writeByteArray(ProtoBuf.encodeToByteArray(SpawnData.serializer(), spawnData))
    }

    override fun getAddEntityPacket(): Packet<ClientGamePacketListener> {
        // Ensures additional spawn data is properly handled
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    fun initializeMovementState(position: Vec3, velocity: Vec3) {
        this.moveTo(position.minusGrenadeSizeOffset())
        trajectory.initialize(position, velocity)
    }

    override fun tick() {
        super.baseTick()

        val lastPos = this.trajectory.nodes.last().position
        val node = this.trajectory.tick(this.level())

        ModLogger.trace(
            "Grenade ${this.id} moved ${
                this.center.minus(node.position).length()
            } blocks last tick, current velocity ${node.velocity.length()}"
        )

        this.moveTo(node.position.minusGrenadeSizeOffset())
        this.deltaMovement = this.center.minus(lastPos)



        if (this.level().isClientSide) {
            if (!trajectory.initialized) {
                ModLogger.warn("Client trajectory not initialized")
            }
        } else {
            val radius = this.level().server!!.playerList.viewDistance.times(16).toDouble()
            ModPacketHandler.INSTANCE.send(
                PacketDistributor.NEAR.with {
                    PacketDistributor.TargetPoint(
                        this.x,
                        this.y,
                        this.z,
                        radius,
                        this.level().dimension(),
                    )
                },
                ServerGrenadeMovementSyncMessage(
                    this.id,
                    node,
                ),
            )
        }
    }

    fun syncServerMovement(id: Int, node: TickNode) {
        this.trajectory.syncServerNode(id, node)
    }


    /**Provide basic hook for playing sound events
     * */
    abstract fun onHitBlock(data: SubtickNode.BlockBounceData)

    abstract fun onHitEntity(data: SubtickNode.EntityBounceData)

    abstract fun onTrajectoryComplete()
}
