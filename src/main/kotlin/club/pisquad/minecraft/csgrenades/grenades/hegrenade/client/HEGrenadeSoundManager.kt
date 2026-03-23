package club.pisquad.minecraft.csgrenades.grenades.hegrenade.client

import club.pisquad.minecraft.csgrenades.grenades.hegrenade.HEGrenadeRegistries
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3

/**
 * Only use on client side!
 */
object HEGrenadeSoundManager {
    fun playExplosionSound(position: Vec3): Boolean {
        val player = Minecraft.getInstance().player!!
        val distance = player.position().distanceTo(position)
        val instance = HEGrenadeRegistries.sounds.explode.getInstance(position, distance) ?: return false
        Minecraft.getInstance().soundManager.play(instance)
        return true
    }
}