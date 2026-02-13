package club.pisquad.minecraft.csgrenades.network.message.firegrenade

import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import net.minecraftforge.network.NetworkDirection
import java.util.Optional

object FireGrenadePacketHandler : CsGrenadePacketHandler {
    override fun registerMessages() {
        ModPacketHandler.registerSingleMessage(
            FireGrenadeActivatedMessage::class.java,
            FireGrenadeActivatedMessage::encoder,
            FireGrenadeActivatedMessage::decoder,
            FireGrenadeActivatedMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
    }
}
