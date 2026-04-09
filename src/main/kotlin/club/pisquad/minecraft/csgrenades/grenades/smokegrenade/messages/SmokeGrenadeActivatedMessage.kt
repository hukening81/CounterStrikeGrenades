package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.api.event.GrenadeActivationEvent
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import kotlinx.serialization.Serializable
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

/* Voxel data is stored as EntityData to allow easier syncing
* Following message is just a notification for client handlers
* */
@Serializable
class SmokeGrenadeActivatedMessage(
    val id: Int,
) {
    companion object : CsGrenadeMessageHandler<SmokeGrenadeActivatedMessage>(SmokeGrenadeActivatedMessage::class) {
        override fun handler(msg: SmokeGrenadeActivatedMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.packetHandled = true

            MinecraftForge.EVENT_BUS.post(GrenadeActivationEvent(GrenadeType.SMOKE_GRENADE, LogicalSide.CLIENT))
        }
    }
}
