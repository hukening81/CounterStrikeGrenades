package club.pisquad.minecraft.csgrenades.grenades.firegrenade.messages

import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeVariant
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

@Serializable
class ServerFireExtinguishMessage(
    @Serializable(Vec3Serializer::class) val position: Vec3,
    val variant: FireGrenadeVariant
) {
    companion object :
        CsGrenadeMessageHandler<ServerFireExtinguishMessage>(ServerFireExtinguishMessage::class) {
        override fun handler(
            msg: ServerFireExtinguishMessage,
            ctx: Supplier<NetworkEvent.Context>
        ) {
            val context = ctx.get()
            context.packetHandled = true
            context.enqueueWork {
                msg.variant.sounds().extinguish.play(msg.position)
            }
        }
    }
}