package club.pisquad.minecraft.csgrenades.grenades.hegrenade.client

import club.pisquad.minecraft.csgrenades.grenades.hegrenade.HEGrenadeSounds
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3

/**
 * Only use on client side!
 */
object HEGrenadeSoundManager {
    fun playExplosionSound(position: Vec3): Boolean {
        val player = Minecraft.getInstance().player!!
        val distance = player.position().distanceTo(position)
        return HEGrenadeSounds.explode.play(position, distance)
    }
}