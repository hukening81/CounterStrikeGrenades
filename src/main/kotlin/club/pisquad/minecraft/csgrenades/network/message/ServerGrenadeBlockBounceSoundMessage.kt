package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.entity.trajectory.SubtickNode
import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.client.sound.GrenadeSoundData
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.sounds.SoundSource
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

@Serializable
class ServerGrenadeBlockBounceSoundMessage(
    val grenadeType: GrenadeType,
    val id: Int,
    val data: SubtickNode.BlockBounceData
) {
    companion object :
        CsGrenadeMessageHandler<ServerGrenadeBlockBounceSoundMessage>(ServerGrenadeBlockBounceSoundMessage::class) {
        override fun handler(
            msg: ServerGrenadeBlockBounceSoundMessage,
            ctx: Supplier<NetworkEvent.Context>
        ) {
            ModLogger.debug("Recieved block bounce sound message from server for ${msg.grenadeType} ${msg.data}")
            val context = ctx.get()
            context.packetHandled = true
            val soundEvent = getBounceSoundEvent(msg.id)
            if (soundEvent == null) {
                ModLogger.warn("Cannot find entity ${msg.grenadeType}(${msg.id}) to play sound for")
            } else {
                val level = Minecraft.getInstance().level
                if (level == null) {
                    ModLogger.warn("Cannot retrieve client level")
                } else {
                    val position = msg.data.position
                    level.playLocalSound(
                        position.x,
                        position.y,
                        position.z,
                        soundEvent.getSoundEvent(),
                        SoundSource.PLAYERS,
                        soundEvent.getVolume(),
                        1.0f,
                        false
                    )
                }
            }
        }

        private fun getBounceSoundEvent(id: Int): GrenadeSoundData? {
            val level: ClientLevel = Minecraft.getInstance().level ?: return null
            val entity: CounterStrikeGrenadeEntity = (level.getEntity(id) as CounterStrikeGrenadeEntity?) ?: return null
            return entity.sounds.hitBlock
        }
    }
}