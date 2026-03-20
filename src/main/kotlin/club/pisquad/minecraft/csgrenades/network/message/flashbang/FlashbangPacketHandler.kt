package club.pisquad.minecraft.csgrenades.network.message.flashbang

import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import net.minecraftforge.network.NetworkDirection
import java.util.*

object FlashbangPacketHandler : CsGrenadePacketHandler {
    override fun registerMessages(handler: ModPacketHandler) {
        handler.registerMessage(
            FlashbangActivatedMessage::class.java,
            FlashbangActivatedMessage::encoder,
            FlashbangActivatedMessage::decoder,
            FlashbangActivatedMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
    }
}