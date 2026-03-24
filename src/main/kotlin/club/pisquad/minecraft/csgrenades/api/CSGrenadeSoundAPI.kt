package club.pisquad.minecraft.csgrenades.api

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.getEarPosition
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3

object CSGrenadeSoundAPI {

    val entity = CSGrenadeEntitySoundAPI
    val item = CSGrenadeItemSoundAPI

    object CSGrenadeEntitySoundAPI {
        fun playHitBlockSound(position: Vec3, grenadeType: GrenadeType): Boolean {
            val data = grenadeType.sounds.get().hitBlock
            return data.play(position)
        }
    }

    object CSGrenadeItemSoundAPI {
        fun playDraw(grenadeType: GrenadeType): Boolean {
            val position = Minecraft.getInstance().player?.getEarPosition() ?: return false
            return grenadeType.sounds.get().draw.play(position)
        }
    }
}