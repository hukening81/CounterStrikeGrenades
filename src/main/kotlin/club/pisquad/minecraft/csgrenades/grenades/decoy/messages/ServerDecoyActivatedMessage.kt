package club.pisquad.minecraft.csgrenades.grenades.decoy.messages

import club.pisquad.minecraft.csgrenades.grenades.decoy.DecoyClientSoundManager
import club.pisquad.minecraft.csgrenades.grenades.decoy.DecoyFakeSoundProvider
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import kotlinx.serialization.Serializable
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

@Serializable
class ServerDecoyActivatedMessage(
    val decoyID: Int,
    val provider: DecoyFakeSoundProvider
) {
    companion object : CsGrenadeMessageHandler<ServerDecoyActivatedMessage>(ServerDecoyActivatedMessage::class) {
        override fun handler(
            msg: ServerDecoyActivatedMessage,
            ctx: Supplier<NetworkEvent.Context>
        ) {
            val context = ctx.get()
            context.packetHandled = true

            context.enqueueWork {
                DecoyClientSoundManager.playSoundFromMessage(msg)
            }
        }
    }
}