package club.pisquad.minecraft.csgrenades.api

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ThrowType
import club.pisquad.minecraft.csgrenades.api.data.GrenadeSpawnContext
import club.pisquad.minecraft.csgrenades.getEarPosition
import club.pisquad.minecraft.csgrenades.getShootOrigin
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.network.message.ClientGrenadeThrowMessage
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3

object CSGrenadeClientAPI {
    public val sound: CSGrenadeClientSoundAPI = CSGrenadeClientSoundAPI
    public val player: CSGrenadeClientPlayerAPI = CSGrenadeClientPlayerAPI
    public val network: CSGrenadeClientNetworkAPI = CSGrenadeClientNetworkAPI

    object CSGrenadeClientSoundAPI {
        fun playHitBlock(position: Vec3, grenade: GrenadeType): Boolean {
            return grenade.properties.sounds.hitBlock.play(position)
        }

        fun playHitEntity(position: Vec3, grenade: GrenadeType): Boolean {
            return grenade.properties.sounds.hitEntity.play(position)
        }

        fun playDraw(grenade: GrenadeType): Boolean {
            val position = Minecraft.getInstance().player?.getEarPosition() ?: return false
            return grenade.properties.sounds.draw.play(position)
        }

        fun playPinPullStart(grenade: GrenadeType): Boolean {
            val position = Minecraft.getInstance().player?.getEarPosition() ?: return false
            return grenade.properties.sounds.pinPullStart.play(position)
        }

        fun playPinPull(grenade: GrenadeType): Boolean {
            val position = Minecraft.getInstance().player?.getEarPosition() ?: return false
            return grenade.properties.sounds.pinPull.play(position)
        }
    }

    object CSGrenadeClientPlayerAPI {
        fun throwGrenadeFromLocalPlayer(throwType: ThrowType, grenadeType: GrenadeType, jumpThrow: Boolean = false) {
            val player = Minecraft.getInstance().player!!
            val direction = player.lookAngle.normalize()
            val speed = direction.scale(throwType.getSpeed()).add(player.deltaMovement)
            val context = GrenadeSpawnContext(
                grenadeType,
                player.uuid,
                player.getShootOrigin(),
                speed
            )
            val message = ClientGrenadeThrowMessage(context, jumpThrow)
            CSGrenadeClientNetworkAPI.sendToServer(message)
        }
    }

    object CSGrenadeClientNetworkAPI {
        fun sendToServer(message: Any) {
            ModPacketHandler.INSTANCE.sendToServer(message)
        }
    }
}