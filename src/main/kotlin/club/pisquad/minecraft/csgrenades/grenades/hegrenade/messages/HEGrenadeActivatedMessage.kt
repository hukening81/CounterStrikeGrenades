package club.pisquad.minecraft.csgrenades.grenades.hegrenade.messages

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.api.event.GrenadeActivationEvent
import club.pisquad.minecraft.csgrenades.client.render.hegrenade.HEGrenadeExplosionData
import club.pisquad.minecraft.csgrenades.client.render.hegrenade.HEGrenadeRenderManager
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.HEGrenadeHelper
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.LogicalSide
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
    companion object : CsGrenadeMessageHandler<HEGrenadeActivatedMessage>(HEGrenadeActivatedMessage::class) {

        override fun handler(msg: HEGrenadeActivatedMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()

            MinecraftForge.EVENT_BUS.post(GrenadeActivationEvent(GrenadeType.HE_GRENADE, LogicalSide.CLIENT))

            val level = Minecraft.getInstance().level ?: return
            HEGrenadeRenderManager.render(HEGrenadeExplosionData(msg.position))

//            HEGrenadeSoundManager.playExplosionSound(msg.position)


            HEGrenadeHelper.blowUpNearbySmokeGrenade(level, msg.position)
            context.packetHandled = true
        }
    }
}