package club.pisquad.minecraft.csgrenades.grenades.firegrenade.renderer

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeVariant
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireRegionEntity
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.flame.FIRE_PARTICLE_Y_SPEED
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeRegionEntity
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelPos
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.Mod
import kotlin.random.Random

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID)
object FlameParticleRenderer {
    @SubscribeEvent
    @JvmStatic
    fun onLevelTick(event: TickEvent.LevelTickEvent) {
        if (event.side != LogicalSide.CLIENT) {
            return
        }
        val particleEngine = Minecraft.getInstance().particleEngine

        FireRegionEntity.clientTrackedEntities.forEach { entity ->
            if (!entity.hasInitialized || entity.debugMode) {
                return@forEach
            }

            entity.flameMap.filter { !SmokeRegionEntity.isVoxelInSmoke(entity.level(), it.key) }.forEach { pos, entry ->
                if (Random.nextDouble() < 0.6) {
                    return@forEach
                }
                val lifetime = entity.flameMap.getParticleLifeTime(pos)
                val particleType = entity.variant.getRandomParticleType()
                val position = pos.randomPositionFromBottom()
                val particle = particleEngine.createParticle(
                    particleType,
                    position.x,
                    position.y,
                    position.z,
                    0.0,
                    FIRE_PARTICLE_Y_SPEED * Random.nextDouble(1.0),
                    0.0
                )?.lifetime = lifetime

            }
        }
    }

    fun renderPopInAir(center: Vec3, variant: FireGrenadeVariant): Boolean {
        val particleEngine = Minecraft.getInstance().particleEngine
        val particleType = variant.getRandomParticleType()
        val speeds = buildSet {
            repeat(500) {
                add(randomShootDirection().scale(Random.nextDouble(0.1, 0.2)))
            }
        }
        speeds.forEach {
            particleEngine.createParticle(
                particleType, center.x, center.y, center.z, it.x, it.y, it.z
            )
        }
        return true
    }
}

private fun VoxelPos.randomPositionFromBottom(): Vec3 {
    val offsetX = Random.nextDouble(0.5)
    val offsetZ = Random.nextDouble(0.5)
    return this.worldPos().add(Vec3(offsetX, 0.0, offsetZ))
}
private fun randomShootDirection(): Vec3 {
    val x = Random.nextDouble(-0.5, 0.5)
    val y = Random.nextDouble(-0.5, 0.5)
    val z = Random.nextDouble(-0.5, 0.5)
    return Vec3(x, y, z).normalize()
}