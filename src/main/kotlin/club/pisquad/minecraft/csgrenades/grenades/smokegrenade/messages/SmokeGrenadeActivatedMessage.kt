package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages

import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.event.SmokeGrenadeActivatedEvent
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelMap
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

/* Voxel data is stored as EntityData to allow easier syncing
* Following message is just a notification for client handlers
* */
@Serializable
class SmokeGrenadeActivatedMessage(
    val grenade: Int,
    @Serializable(with = UUIDSerializer::class) val ownerUUID: UUID,
    val voxels: VoxelMap,
) {
    companion object : CsGrenadeMessageHandler<SmokeGrenadeActivatedMessage>(SmokeGrenadeActivatedMessage::class) {
        override fun handler(msg: SmokeGrenadeActivatedMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.packetHandled = true

            (Minecraft.getInstance().level!!.getEntity(msg.grenade) as CounterStrikeGrenadeEntity?).run {
                MinecraftForge.EVENT_BUS.post(
                    SmokeGrenadeActivatedEvent(
                        LogicalSide.CLIENT,
                        msg.ownerUUID,
                        this,
                        msg.voxels
                    )
                )
            }
        }
    }
}
