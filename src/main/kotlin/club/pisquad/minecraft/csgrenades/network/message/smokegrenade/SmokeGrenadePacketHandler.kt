package club.pisquad.minecraft.csgrenades.network.message.smokegrenade

import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import net.minecraftforge.network.NetworkDirection
import java.util.Optional

object SmokeGrenadePacketHandler : CsGrenadePacketHandler {
    override fun registerMessages(handler: ModPacketHandler) {
        handler.registerMessage(
            SmokeGrenadeActivatedMessage::class.java,
            SmokeGrenadeActivatedMessage::encoder,
            SmokeGrenadeActivatedMessage::decoder,
            SmokeGrenadeActivatedMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
    }
}
