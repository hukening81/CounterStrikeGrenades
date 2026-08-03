package club.pisquad.minecraft.csgrenades.grenades.hegrenade.messages

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.api.event.GrenadeActivatedEvent
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.client.HEGrenadeExplosionRenderer
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.client.HEGrenadeSoundManager
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

/**
 * This message is sent to help client render the explosion effect
 *
 * Direction: serve -> client
 * */
@Serializable
class HEGrenadeActivatedMessage(
    @Serializable(with = UUIDSerializer::class) val ownerUUID: UUID,
    @Serializable(with = Vec3Serializer::class) val position: Vec3,
) {
    companion object : CsGrenadeMessageHandler<HEGrenadeActivatedMessage>(HEGrenadeActivatedMessage::class) {

        override fun handler(msg: HEGrenadeActivatedMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.packetHandled = true

            context.enqueueWork {

            MinecraftForge.EVENT_BUS.post(
                GrenadeActivatedEvent(
                    LogicalSide.CLIENT,
                    GrenadeType.HE_GRENADE,
                    msg.ownerUUID, msg.position
                )
            )

                val level = Minecraft.getInstance().level?:return@enqueueWork
                HEGrenadeExplosionRenderer.renderSingle(msg.position)

                HEGrenadeSoundManager.playExplosionSound(msg.position)
            }
        }
    }
}