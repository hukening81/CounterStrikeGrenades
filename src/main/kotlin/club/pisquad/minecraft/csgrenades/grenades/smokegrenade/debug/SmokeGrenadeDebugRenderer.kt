package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.debug

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeEntity
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.phys.AABB
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object SmokeGrenadeDebugRenderer {

    private fun getNearbySmokeGrenade(): List<SmokeGrenadeEntity> {
        val player = Minecraft.getInstance().player!!
        val horizontalDistance = Minecraft.getInstance().options.renderDistance().get() * 16.0
        val verticalDistance = 256.0

        val entities = player.level()
            .getEntities(
                null, AABB.ofSize(
                    player.position(),
                    horizontalDistance, verticalDistance, horizontalDistance
                )
            ) { it is SmokeGrenadeEntity }.map { it as SmokeGrenadeEntity }

        return entities
    }

    @JvmStatic
    @SubscribeEvent
    fun renderVoxelOutline(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return
        }
        if (!SmokeGrenadeDebugState.showVoxelOutline) {
            return
        }
        val poseStack = event.poseStack
        val camera = event.camera

        poseStack.pushPose()
        poseStack.translate(-camera.position.x, -camera.position.y, -camera.position.z)

        RenderSystem.disableDepthTest()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        val smokes = getNearbySmokeGrenade()
        val bufferSource = Minecraft.getInstance().renderBuffers().bufferSource()

        smokes.forEach { smoke ->
            smoke.getVoxels()?.keys?.forEach { pos ->
                val buffer = bufferSource.getBuffer(RenderType.lines())
                LevelRenderer.renderLineBox(
                    poseStack,
                    buffer,
                    pos.x.toDouble(),
                    pos.y.toDouble(),
                    pos.z.toDouble(),
                    pos.x + 1.0,
                    pos.y + 1.0,
                    pos.z + 1.0,
                    1f,
                    1f,
                    1f,
                    1f
                )
                bufferSource.endLastBatch()
            }
        }
        poseStack.popPose()
        
        RenderSystem.disableBlend()
        RenderSystem.enableDepthTest()


    }
}