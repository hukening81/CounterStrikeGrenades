package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.core.entity.trajectory.SubtickNode
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
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
            ModLogger.debug("Recieved block bounce sound message from server for ${msg.grenadeType} ${msg.data}")
            val context = ctx.get()
            context.packetHandled = true
            val data = msg.grenadeType.sounds.get().hitBlock

            val level = Minecraft.getInstance().level
            if (level == null) {
                ModLogger.warn("Cannot retrieve client level")
            } else {
                data.play(msg.data.position)

            }
        }
    }
}