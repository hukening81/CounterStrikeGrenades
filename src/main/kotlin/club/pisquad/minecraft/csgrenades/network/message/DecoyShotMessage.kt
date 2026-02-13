package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.client.sound.DecoySoundController
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class DecoyShotMessage(
    val entityId: Int,
    val gunId: String,
    val customSound: String? = null,
) {
    companion object {
        fun encoder(msg: DecoyShotMessage, buffer: FriendlyByteBuf) {
            buffer.writeInt(msg.entityId)
            buffer.writeUtf(msg.gunId)
            buffer.writeBoolean(msg.customSound != null)
            if (msg.customSound != null) {
                buffer.writeUtf(msg.customSound)
            }
        }

        fun decoder(buffer: FriendlyByteBuf): DecoyShotMessage {
            val entityId = buffer.readInt()
            val gunId = buffer.readUtf()
            val hasCustomSound = buffer.readBoolean()
            val customSound = if (hasCustomSound) buffer.readUtf() else null
            return DecoyShotMessage(entityId, gunId, customSound)
        }

        fun handler(msg: DecoyShotMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.packetHandled = true
            if (context.direction.receptionSide.isClient) {
                DecoySoundController.playShotSound(msg.entityId, msg.gunId, msg.customSound)
            }
        }
    }
}
