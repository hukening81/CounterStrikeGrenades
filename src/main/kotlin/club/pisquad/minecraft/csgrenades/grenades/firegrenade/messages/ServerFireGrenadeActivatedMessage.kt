package club.pisquad.minecraft.csgrenades.grenades.firegrenade.messages

import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeVariant
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.flame.FlameMap
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.renderer.FlameParticleRenderer
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

@Serializable
class ServerFireGrenadeActivatedMessage(
    val entityID: Int,
    val variant: FireGrenadeVariant,
    val reason: ActivateReason,
) {
    companion object :
        CsGrenadeMessageHandler<ServerFireGrenadeActivatedMessage>(ServerFireGrenadeActivatedMessage::class) {
        override fun handler(
            msg: ServerFireGrenadeActivatedMessage,
            ctx: Supplier<NetworkEvent.Context>
        ) {
            val context = ctx.get()
            context.packetHandled = true
            when (msg.reason) {
                is ActivateReason.PopInAir -> {
                    context.enqueueWork {
                        FlameParticleRenderer.renderPopInAir(msg.reason.position, msg.variant)
                        msg.variant.sounds().detonateAir.play(msg.reason.position)
                    }
                }

                is ActivateReason.SmashInSmoke -> {
                    context.enqueueWork {
                        msg.variant.sounds().extinguish.play(msg.reason.position)
                    }
                }

                is ActivateReason.SmashOnGround -> {
                    context.enqueueWork {
                        msg.variant.sounds().smash.play(msg.reason.position)
                    }
                }

            }
        }
    }
}

@Serializable
sealed interface ActivateReason {
    @Serializable
    class PopInAir(
        @Serializable(with = Vec3Serializer::class) val position: Vec3,
    ) : ActivateReason {}

    @Serializable
    class SmashOnGround(
        @Serializable(with = Vec3Serializer::class) val position: Vec3,
        val flameMap: FlameMap
    ) : ActivateReason {}

    @Serializable
    class SmashInSmoke(
        @Serializable(with = Vec3Serializer::class) val position: Vec3,
    ) : ActivateReason {}
}