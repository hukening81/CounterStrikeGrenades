package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.entity.core.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.entity.core.trajectory.TickNode
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

@Serializable
class ServerGrenadeMovementSyncMessage(
    val id: Int,
    val node: TickNode,
) {
    companion object :
        CsGrenadeMessageHandler<ServerGrenadeMovementSyncMessage>(ServerGrenadeMovementSyncMessage::class) {
        override fun handler(msg: ServerGrenadeMovementSyncMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            // Serverside should ensure grenade entity is loaded on client side
            context.enqueueWork {
                val grenade = Minecraft.getInstance().level?.getEntity(msg.id)
                if (grenade != null) {
                    grenade as CounterStrikeGrenadeEntity
                    grenade.syncServerMovement(msg.id, msg.node)
                }
            }

            context.packetHandled = true
        }
    }
}
