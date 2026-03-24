package club.pisquad.minecraft.csgrenades.grenades.flashbang.client

import club.pisquad.minecraft.csgrenades.getEarPosition
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangRegistries
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3

object FlashbangSoundManager {
    fun playExplosionSound(position: Vec3): Boolean {
        val player = Minecraft.getInstance().player ?: return false
        return FlashbangRegistries.sounds.explode.play(position, player.getEarPosition().distanceTo(position))
    }
}