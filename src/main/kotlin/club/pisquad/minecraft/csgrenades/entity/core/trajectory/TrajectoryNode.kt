package club.pisquad.minecraft.csgrenades.entity.core.trajectory

import club.pisquad.minecraft.csgrenades.entity.core.trajectory.PhysicsHelper.getBlocksInPath
import club.pisquad.minecraft.csgrenades.math.Segment
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.cbor.Cbor
import net.minecraft.core.Direction
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.math.absoluteValue

interface TrajectoryNode {
    val position: Vec3
    val velocity: Vec3

    @Serializable
    class TickNode(
        val tick: Int,
        @Serializable(with = Vec3Serializer::class) override val position: Vec3,
        @Serializable(with = Vec3Serializer::class) override val velocity: Vec3,
        @Transient val subtickNodes: MutableList<SubtickNode> = mutableListOf(),
    ) : TrajectoryNode {
        class TickNodeEntityDataSerializer : EntityDataSerializer<TickNode> {
            @OptIn(ExperimentalSerializationApi::class)
            override fun write(buffer: FriendlyByteBuf, value: TickNode) {
                buffer.writeByteArray(Cbor.encodeToByteArray(serializer(), value))
            }

            @OptIn(ExperimentalSerializationApi::class)
            override fun read(buffer: FriendlyByteBuf): TickNode {
                return Cbor.decodeFromByteArray(serializer(), buffer.readByteArray())
            }

            override fun copy(value: TickNode): TickNode {
                return value
            }
        }

        companion object {
            fun empty(): TickNode {
                return TickNode(0, Vec3.ZERO, Vec3.ZERO)
            }
        }

        /**Calculates the subtick nodes within this tick
         * @return Next TickNode
         * */
        fun processTick(level: Level, bounceCB: Function2<Vec3, Direction, Unit>, hitEntityCB: Function3<Vec3, Direction, Entity, Unit>): TickNode {
            var partialTick = 0.0
            var lastNode: SubtickNode = this.toSubtickNode()
            while (true) {
                lastNode = tryTravelInDirection(level, lastNode.position, lastNode.velocity, partialTick, bounceCB, hitEntityCB)

                if (lastNode.partialTick.minus(partialTick).absoluteValue < 0.001) {
                    // Somehow we stuck here?
                    // Grenade keep bouncing between two adjacent surfaces
                    return TickNode(this.tick + 1, lastNode.velocity, Vec3.ZERO)
                }
                partialTick = lastNode.partialTick
                if (partialTick > 1) {
                    return TickNode(
                        this.tick + 1,
                        lastNode.position,
                        lastNode.velocity,
                    )
                } else {
                    this.subtickNodes.add(
                        lastNode,
                    )
                }
            }


        }

        fun toSubtickNode(): SubtickNode {
            return SubtickNode(
                this.position,
                this.velocity,
                0.0,
            )
        }

        private fun tryTravelInDirection(level: Level, position: Vec3, velocity: Vec3, partialTick: Double, bounceCB: (Vec3, Direction) -> Unit, hitEntityCB: (Vec3, Direction, Entity) -> Unit): SubtickNode {
            val deltaMovement = velocity.scale(1 - partialTick)
            if (deltaMovement.length() > velocity.length()) {
                println("lll")
            }
            val blocksInPath = getBlocksInPath(
                Segment(
                    position, position.add(deltaMovement),
                ),
            ).filter { BounceHelper.shouldBounceOnBlock(level, it) }

            for (block in blocksInPath) {
                val bounceResult = BounceHelper.tryBounce(level, block, position, deltaMovement, velocity)
                if (bounceResult.type == BounceHelper.BounceResultTypes.THROUGH) {
                    continue
                } else {
                    bounceCB(bounceResult.bouncePoint, bounceResult.direction!!)
                    return SubtickNode(
                        bounceResult.bouncePoint,
                        bounceResult.newVelocity,
                        partialTick + bounceResult.tickDelta,
                    )
                }
            }
            return SubtickNode(position.add(deltaMovement), PhysicsHelper.applyVelocityPhysics(velocity, 1.0), Double.MAX_VALUE)

        }


    }

    data class SubtickNode(
        override val position: Vec3,
        override val velocity: Vec3,
        val partialTick: Double,
    ) : TrajectoryNode
}
