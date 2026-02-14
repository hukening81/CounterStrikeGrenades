package club.pisquad.minecraft.csgrenades.network.message.hegrenade

import club.pisquad.minecraft.csgrenades.network.*
import net.minecraftforge.network.NetworkDirection
import java.util.*

object HEGrenadePacketHandler : CsGrenadePacketHandler {
    override fun registerMessages(handler: ModPacketHandler) {
        handler.registerMessage(
            HEGrenadeActivatedMessage::class.java,
            HEGrenadeActivatedMessage::encoder,
            HEGrenadeActivatedMessage::decoder,
            HEGrenadeActivatedMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
    }
}
