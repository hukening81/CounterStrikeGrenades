package club.pisquad.minecraft.csgrenades.grenades.hegrenade

import club.pisquad.minecraft.csgrenades.grenades.hegrenade.messages.HEGrenadeActivatedMessage
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import net.minecraftforge.network.NetworkDirection
import java.util.*

object HEGrenadePacketHandler : CsGrenadePacketHandler {
    override fun registerMessages(handler: ModPacketHandler) {
        handler.registerMessage(
            HEGrenadeActivatedMessage::class.java,
            HEGrenadeActivatedMessage.Companion::encoder,
            HEGrenadeActivatedMessage.Companion::decoder,
            HEGrenadeActivatedMessage.Companion::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
    }
}