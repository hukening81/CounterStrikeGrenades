package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

@Serializable
class ServerGrenadeMovementSyncMessage(
    val entityId: Int,
    val tick:Int,
    val completed: Boolean,
    @Serializable(with = Vec3Serializer::class) val position: Vec3,
    @Serializable(with = Vec3Serializer::class) val velocity: Vec3,
) {
    companion object :
        CsGrenadeMessageHandler<ServerGrenadeMovementSyncMessage>(ServerGrenadeMovementSyncMessage::class) {
        override fun handler(msg: ServerGrenadeMovementSyncMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            // Serverside should ensure grenade entity is loaded on client side
            context.enqueueWork {
                val grenade = Minecraft.getInstance().level?.getEntity(msg.entityId)
                if (grenade != null) {
                    grenade as CounterStrikeGrenadeEntity
                    grenade.syncServerMovement(msg)
                }
            }

            context.packetHandled = true
        }
    }
}
