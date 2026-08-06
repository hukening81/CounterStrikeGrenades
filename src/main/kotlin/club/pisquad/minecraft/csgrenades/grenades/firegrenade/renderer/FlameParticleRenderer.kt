package club.pisquad.minecraft.csgrenades.grenades.firegrenade.renderer

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireRegionEntity
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.flame.FIRE_PARTICLE_Y_SPEED
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
            entity.flameMap.forEach { pos, entry ->
                if (Random.nextDouble() < 0.7) {
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
                    FIRE_PARTICLE_Y_SPEED,
                    0.0
                )?.lifetime = lifetime

            }
        }
    }
}

private fun VoxelPos.randomPositionFromBottom(): Vec3 {
    val offsetX = Random.nextDouble(0.5)
    val offsetZ = Random.nextDouble(0.5)
    return this.worldPos().add(Vec3(offsetX, 0.0, offsetZ))
}