package club.pisquad.minecraft.csgrenades.network.message.smokegrenade

import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.data.RoundedVec3
import kotlinx.serialization.Serializable
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

@Serializable
class SmokeGrenadeActivatedMessage(
    val points: List<RoundedVec3>,
) {
    companion object : CsGrenadeMessageHandler<SmokeGrenadeActivatedMessage>(SmokeGrenadeActivatedMessage::class) {
        override fun handler(msg: SmokeGrenadeActivatedMessage, ctx: Supplier<NetworkEvent.Context>) {
            println("SmokeGrenadeActivatedMessage ${msg.points.size}")
        }
    }
}
