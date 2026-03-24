package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.entity.trajectory.SubtickNode
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
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
            val data = getBounceSoundEvent(msg.id)

            if (data == null) {
                ModLogger.warn("Cannot find entity ${msg.grenadeType}(${msg.id}) to play sound for")
            } else {
                val level = Minecraft.getInstance().level
                if (level == null) {
                    ModLogger.warn("Cannot retrieve client level")
                } else {
                    data.play(msg.data.position)
//                    val position = msg.data.position
//                    val player = Minecraft.getInstance().player ?: return
//                    //TODO(hukening81): replace with a spatial sound
//                    level.playSeededSound(
//                        player,
//                        position.x,
//                        position.y,
//                        position.z,
//                        data.soundEvent,
//                        SoundSource.PLAYERS,
//                        10.0f,
//                        1.0f,
//                        Random.nextLong()
//                    )
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