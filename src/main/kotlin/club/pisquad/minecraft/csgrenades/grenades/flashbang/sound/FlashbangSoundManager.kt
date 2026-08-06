package club.pisquad.minecraft.csgrenades.grenades.flashbang.sound

import club.pisquad.minecraft.csgrenades.getEarPosition
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangSounds
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3

object FlashbangSoundManager {
    fun playExplosionSound(position: Vec3): Boolean {
        val player = Minecraft.getInstance().player ?: return false
        return FlashbangSounds.explode.play(position, player.getEarPosition().distanceTo(position))
    }
}