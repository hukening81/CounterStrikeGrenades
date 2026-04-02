package club.pisquad.minecraft.csgrenades.api

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.getEarPosition
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3

object CSGrenadeClientAPI {
    val sound = CSGrenadeClientSoundAPI

    object CSGrenadeClientSoundAPI {
        fun playHitBlock(position: Vec3, grenadeType: GrenadeType): Boolean {
            val data = grenadeType.sounds.get().hitBlock
            return data.play(position)
        }

        fun playDraw(grenadeType: GrenadeType): Boolean {
            val position = Minecraft.getInstance().player?.getEarPosition() ?: return false
            return grenadeType.sounds.get().draw.play(position)
        }
    }
}