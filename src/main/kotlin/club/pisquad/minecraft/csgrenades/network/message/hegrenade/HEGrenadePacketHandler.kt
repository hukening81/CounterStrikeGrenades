package club.pisquad.minecraft.csgrenades.network.message.hegrenade

import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import net.minecraftforge.network.NetworkDirection
import java.util.Optional

object HEGrenadePacketHandler : CsGrenadePacketHandler {
    override fun registerMessages() {
        ModPacketHandler.registerSingleMessage(
            HEGrenadeActivatedMessage::class.java,
            HEGrenadeActivatedMessage::encoder,
            HEGrenadeActivatedMessage::decoder,
            HEGrenadeActivatedMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
    }
}
