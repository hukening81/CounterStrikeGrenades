package club.pisquad.minecraft.csgrenades.client.render.smokegrenade

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.entity.smokegrenade.*
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object SmokeGrenadeSmokeRenderer {
    @JvmStatic
    @SubscribeEvent
    fun onRender(event: RenderLevelStageEvent) {
        val minecraft = Minecraft.getInstance()
        val player = minecraft.player!!
        val level = player.level()
        val renderDistanceMeter = minecraft.options.renderDistance().get().times(16)
        val activatedSmokes = level.getEntitiesOfClass(SmokeGrenadeEntity::class.java, player.boundingBox.inflate(renderDistanceMeter.toDouble())) {
            it.isActivated()
        }
        activatedSmokes.forEach { smoke ->
            smoke.getSmokeDataPoints().forEach { point ->
                run {
                    minecraft.particleEngine.createParticle(
                        ParticleTypes.ASH,
                        point.position.x.toDouble(),
                        point.position.y.toDouble(),
                        point.position.z.toDouble(),
                        0.0,
                        0.0,
                        0.0,
                    )?.scale(4f)?.lifetime = 1
                }
            }
        }
    }
}
