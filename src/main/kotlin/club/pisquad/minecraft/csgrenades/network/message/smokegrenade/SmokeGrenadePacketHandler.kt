package club.pisquad.minecraft.csgrenades.network.message.smokegrenade

import club.pisquad.minecraft.csgrenades.network.*
import net.minecraftforge.network.NetworkDirection
import java.util.*

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
