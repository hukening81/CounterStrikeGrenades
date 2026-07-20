package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.api.event.GrenadeHitBlockEvent
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.entity.HitBlockHandleResult
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.BlockPosSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import club.pisquad.minecraft.csgrenades.physics.GrenadeHitBlock
import club.pisquad.minecraft.csgrenades.physics.GrenadeVelocity
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

@Serializable
data class ServerGrenadeHitBlockMessage(
    val grenade: Int, val grenadeType: GrenadeType,
    @Serializable(with = UUIDSerializer::class) val ownerUUID: UUID,
    @Serializable(with = BlockPosSerializer::class) val blockPos: BlockPos,
    @Serializable(with = Vec3Serializer::class) val hitPoint: Vec3,
    val velocity: GrenadeVelocity,
    val handleResult: HitBlockHandleResult,
) {
    companion object : CsGrenadeMessageHandler<ServerGrenadeHitBlockMessage>(ServerGrenadeHitBlockMessage::class) {
        override fun handler(
            msg: ServerGrenadeHitBlockMessage, ctx: Supplier<NetworkEvent.Context>
        ) {
            val context = ctx.get()
            context.packetHandled = true
            ModLogger.debug("Received block bounce sound message from server for ${msg.grenadeType} ${msg.hitPoint}")

            // Run on client
            context.enqueueWork {
                val grenade: CounterStrikeGrenadeEntity? =
                    Minecraft.getInstance().level!!.getEntity(msg.grenade) as CounterStrikeGrenadeEntity?
                val event = GrenadeHitBlockEvent(
                    LogicalSide.CLIENT,
                    msg.grenadeType,
                    msg.ownerUUID,
                    grenade,
                    msg.blockPos,
                    msg.hitPoint,
                    msg.velocity
                )
                MinecraftForge.EVENT_BUS.post(event)
            }
        }

        fun create(
            grenade: CounterStrikeGrenadeEntity,
            data: GrenadeHitBlock,
            handleResult: HitBlockHandleResult,
        ): ServerGrenadeHitBlockMessage {
            return ServerGrenadeHitBlockMessage(
                grenade.id,
                grenade.grenadeType,
                grenade.ownerUuid,
                data.blockPos,
                data.hitPoint,
                data.velocity,
                handleResult
            )
        }
    }
}