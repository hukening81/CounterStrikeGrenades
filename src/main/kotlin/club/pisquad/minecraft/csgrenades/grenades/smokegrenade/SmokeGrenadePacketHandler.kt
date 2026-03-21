package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages.SmokeGrenadeActivatedMessage
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import net.minecraftforge.network.NetworkDirection
import java.util.*

object SmokeGrenadePacketHandler : CsGrenadePacketHandler {
    override fun registerMessages(handler: ModPacketHandler) {
        handler.registerMessage(
            SmokeGrenadeActivatedMessage::class.java,
            SmokeGrenadeActivatedMessage.Companion::encoder,
            SmokeGrenadeActivatedMessage.Companion::decoder,
            SmokeGrenadeActivatedMessage.Companion::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
    }
}