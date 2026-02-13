package club.pisquad.minecraft.csgrenades.network.message.hegrenade

import club.pisquad.minecraft.csgrenades.client.render.hegrenade.HEGrenadeExplosionData
import club.pisquad.minecraft.csgrenades.client.render.hegrenade.HEGrenadeRenderManager
import club.pisquad.minecraft.csgrenades.entity.grenade.HEGrenadeHelper
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

/**
 * This message is sent to help client render the explosion effect
 *
 * Direction: serve -> client
 * */
@Serializable
class HEGrenadeActivatedMessage(
    @Serializable(with = Vec3Serializer::class) val position: Vec3,

) {
    companion object : CsGrenadeMessageHandler<HEGrenadeActivatedMessage> {
        override fun encoder(message: HEGrenadeActivatedMessage, buffer: FriendlyByteBuf) {
            buffer.writeUtf(Json.encodeToString(message))
        }

        override fun decoder(buffer: FriendlyByteBuf): HEGrenadeActivatedMessage = Json.decodeFromString<HEGrenadeActivatedMessage>(buffer.readUtf())

        override fun handler(msg: HEGrenadeActivatedMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            // Server should only notify players within the same dimension as the grenade entity
            // Thus the following grenade entity is guaranteed to be in this dimension (level)
            val level = Minecraft.getInstance().level ?: return
            HEGrenadeRenderManager.render(HEGrenadeExplosionData(msg.position))
            HEGrenadeHelper.blowUpNearbySmokeGrenade(level, msg.position)
            context.packetHandled = true
        }
    }
}
