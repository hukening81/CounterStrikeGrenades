package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.grenades.decoy.messages.ServerDecoyActivatedMessage
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import net.minecraftforge.network.NetworkDirection
import java.util.*

object DecoyPacketHandler {
    fun registerMessages(handler: ModPacketHandler) {
        handler.registerMessage(
            ServerDecoyActivatedMessage::class.java,
            ServerDecoyActivatedMessage::encoder,
            ServerDecoyActivatedMessage::decoder,
            ServerDecoyActivatedMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
    }
}
