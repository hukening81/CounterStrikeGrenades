package club.pisquad.minecraft.csgrenades.network.message.smokegrenade

import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import kotlinx.serialization.Serializable
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

@Serializable
class SmokeGrenadeActivatedMessage {
    // Due to implementation change, this message is not being used
    companion object : CsGrenadeMessageHandler<SmokeGrenadeActivatedMessage>(SmokeGrenadeActivatedMessage::class) {
        override fun handler(msg: SmokeGrenadeActivatedMessage, ctx: Supplier<NetworkEvent.Context>) {
//            println("SmokeGrenadeActivatedMessage")
        }
    }
}
