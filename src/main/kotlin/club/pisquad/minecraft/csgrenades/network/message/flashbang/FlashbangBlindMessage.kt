package club.pisquad.minecraft.csgrenades.network.message.flashbang

import club.pisquad.minecraft.csgrenades.client.render.flashbang.BlindEffectRenderManager
import club.pisquad.minecraft.csgrenades.entity.flashbang.FlashbangBlindEffectData
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

@Serializable
data class FlashbangActivatedMessage(
    val data: Map<@Serializable(with = UUIDSerializer::class) UUID, FlashbangBlindEffectData>,
) {
    companion object : CsGrenadeMessageHandler<FlashbangActivatedMessage>(FlashbangActivatedMessage::class) {
        override fun handler(
            msg: FlashbangActivatedMessage,
            ctx: Supplier<NetworkEvent.Context>
        ) {
            val context = ctx.get()
            context.packetHandled = true

            val localUUID = Minecraft.getInstance().player!!.uuid

            msg.data.forEach { (uuid, data) ->
                if (uuid == localUUID) {
                    BlindEffectRenderManager.schedule(data)
                } else {
                    throw NotImplementedError()
                }
            }
        }

    }
}


