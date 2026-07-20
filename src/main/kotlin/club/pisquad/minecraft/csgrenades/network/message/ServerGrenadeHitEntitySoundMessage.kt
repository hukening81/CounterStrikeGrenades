package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.entity.GrenadeEntityData
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import club.pisquad.minecraft.csgrenades.physics.GrenadeHitEntity
import kotlinx.serialization.Serializable
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

@Serializable
class ServerGrenadeHitEntitySoundMessage(
    val grenadeType: GrenadeType,
    @Serializable(with = Vec3Serializer::class) val position: Vec3
) {
    companion object :
        CsGrenadeMessageHandler<ServerGrenadeHitEntitySoundMessage>(ServerGrenadeHitEntitySoundMessage::class) {
        override fun handler(
            msg: ServerGrenadeHitEntitySoundMessage,
            ctx: Supplier<NetworkEvent.Context>
        ) {
            val context = ctx.get()
            context.packetHandled = true
            context.enqueueWork {
                msg.grenadeType.implementation.getCommonSounds().hitEntity.play(msg.position)
            }

        }

        fun createAndSend(grenade: CounterStrikeGrenadeEntity, data: GrenadeHitEntity) {
            val message = ServerGrenadeHitEntitySoundMessage(grenade.grenadeType, data.hitPoint)
            ModPacketHandler.sendMessageToPlayer(grenade.level() as ServerLevel, grenade.center, message)
        }
    }
}