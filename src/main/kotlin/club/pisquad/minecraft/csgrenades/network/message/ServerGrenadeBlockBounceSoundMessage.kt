package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.api.CSGrenadesAPI
import club.pisquad.minecraft.csgrenades.core.entity.trajectory.SubtickNode
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

@Serializable
class ServerGrenadeBlockBounceSoundMessage(
    val grenadeType: GrenadeType,
    @Serializable(with = UUIDSerializer::class) val uuid: UUID,
    val data: SubtickNode.BlockBounceData
) {
    companion object :
        CsGrenadeMessageHandler<ServerGrenadeBlockBounceSoundMessage>(ServerGrenadeBlockBounceSoundMessage::class) {
        override fun handler(
            msg: ServerGrenadeBlockBounceSoundMessage,
            ctx: Supplier<NetworkEvent.Context>
        ) {
            val context = ctx.get()
            context.packetHandled = true
            ModLogger.debug("Recieved block bounce sound message from server for ${msg.grenadeType} ${msg.data}")
            val data = msg.grenadeType.sounds.get().hitBlock
            CSGrenadesAPI.sound.entity.playHitBlockSound(msg.data.position, msg.grenadeType)
        }
    }
}