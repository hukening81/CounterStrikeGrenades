package club.pisquad.minecraft.csgrenades.network.message

//import club.pisquad.minecraft.csgrenades.client.input.InputState.calculateGrenadeSpeed
import club.pisquad.minecraft.csgrenades.api.CSGrenadesAPI
import club.pisquad.minecraft.csgrenades.api.data.GrenadeSpawnContext
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import kotlinx.serialization.Serializable
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

@Serializable
data class ClientGrenadeThrowMessage(
    val context: GrenadeSpawnContext,
    val jumpThrow: Boolean
) {
    companion object : CsGrenadeMessageHandler<ClientGrenadeThrowMessage>(ClientGrenadeThrowMessage::class) {
        override fun handler(msg: ClientGrenadeThrowMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.packetHandled = true
            context.enqueueWork {
                val player = context.sender ?: return@enqueueWork

                val removeItem = !player.isCreative
                CSGrenadesAPI.server.entity.spawnGrenade(player, msg.context, removeItem)
            }
        }
    }
}
