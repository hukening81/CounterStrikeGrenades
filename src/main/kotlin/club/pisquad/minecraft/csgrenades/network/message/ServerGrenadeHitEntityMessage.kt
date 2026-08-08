package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.api.CSGrenadeClientAPI
import club.pisquad.minecraft.csgrenades.api.event.GrenadeHitEntityEvent
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import club.pisquad.minecraft.csgrenades.physics.GrenadeHitSomething.GrenadeHitEntity
import club.pisquad.minecraft.csgrenades.physics.GrenadeVelocity
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

@Serializable
class ServerGrenadeHitEntityMessage(
    val grenade: Int,
    val grenadeType: GrenadeType,

    @Serializable(with = UUIDSerializer::class)
    val ownerUUID: UUID,

    val entity: Int,

    @Serializable(with = Vec3Serializer::class)
    val hitPoint: Vec3,

    val velocity: GrenadeVelocity
) {
    companion object :
        CsGrenadeMessageHandler<ServerGrenadeHitEntityMessage>(ServerGrenadeHitEntityMessage::class) {
        override fun handler(
            msg: ServerGrenadeHitEntityMessage,
            ctx: Supplier<NetworkEvent.Context>
        ) {
            val context = ctx.get()
            context.packetHandled = true
            // Run on client
            context.enqueueWork {
                val grenade: CounterStrikeGrenadeEntity? =
                    Minecraft.getInstance().level!!.getEntity(msg.grenade) as CounterStrikeGrenadeEntity?
                val entity = Minecraft.getInstance().level!!.getEntity(msg.entity)
                val event = GrenadeHitEntityEvent(
                    LogicalSide.CLIENT,
                    msg.grenadeType,
                    msg.ownerUUID,
                    grenade,
                    entity,
                    msg.hitPoint,
                    msg.velocity
                )
                MinecraftForge.EVENT_BUS.post(event)

                CSGrenadeClientAPI.CSGrenadeClientSoundAPI.playHitEntity(msg.hitPoint, msg.grenadeType)
            }
        }

        fun create(grenade: CounterStrikeGrenadeEntity, data: GrenadeHitEntity): ServerGrenadeHitEntityMessage {
            return ServerGrenadeHitEntityMessage(
                grenade.id,
                grenade.grenadeType,
                grenade.ownerUuid,
                data.entity.id,
                data.hitPoint,
                data.velocity
            )
        }
    }
}