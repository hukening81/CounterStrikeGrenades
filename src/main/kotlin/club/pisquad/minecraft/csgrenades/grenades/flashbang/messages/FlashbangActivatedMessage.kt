package club.pisquad.minecraft.csgrenades.grenades.flashbang.messages

import club.pisquad.minecraft.csgrenades.client.render.flashbang.BlindEffectRenderManager
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangBlindEffectData
import club.pisquad.minecraft.csgrenades.grenades.flashbang.client.FlashbangSoundManager
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

@Serializable
data class FlashbangActivatedMessage(
    @Serializable(with = Vec3Serializer::class) val position: Vec3,
    val blinds: Map<@Serializable(with = UUIDSerializer::class) UUID, FlashbangBlindEffectData>,
) {
    companion object : CsGrenadeMessageHandler<FlashbangActivatedMessage>(FlashbangActivatedMessage::class) {
        override fun handler(
            msg: FlashbangActivatedMessage,
            ctx: Supplier<NetworkEvent.Context>
        ) {
            val context = ctx.get()
            context.packetHandled = true

            val localUUID = Minecraft.getInstance().player!!.uuid

            msg.blinds.forEach { (uuid, data) ->
                FlashbangSoundManager.playExplosionSound(msg.position)
                if (uuid == localUUID) {
                    BlindEffectRenderManager.schedule(data)
                } else {
                    throw NotImplementedError()
                }
            }
        }

    }
}


