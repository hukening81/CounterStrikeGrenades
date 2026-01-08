package club.pisquad.minecraft.csgrenades.client.render

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.client.input.ThrowActionHandler
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.linearInterpolate
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.time.Duration
import java.time.Instant

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object TrajectoryRenderer {

    private const val SIMULATION_TICKS = 100
    private const val GRAVITY = 0.03f

    @SubscribeEvent
    fun onRenderLevelStage(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return
        }

        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        val itemInHand = player.mainHandItem
        val item = itemInHand.item

        if (item !is CounterStrikeGrenadeItem) return
        if (!mc.options.renderDebug) return

        val throwSpeed = ThrowActionHandler.currentThrowSpeed ?: return
        val chargeStart = ThrowActionHandler.chargeStartTime ?: return
        val chargeDuration = Duration.between(chargeStart, Instant.now()).toMillis()
        if (chargeDuration < 1000) return

        val poseStack = event.poseStack
        val camera = mc.gameRenderer.mainCamera
        val cameraPos = camera.position

        val speedFactor =
            (throwSpeed - ModConfig.THROW_SPEED_WEAK.get()) / (ModConfig.THROW_SPEED_STRONG.get() - ModConfig.THROW_SPEED_WEAK.get())
        val playerSpeedFactor =
            linearInterpolate(
                ModConfig.PLAYER_SPEED_FACTOR_WEAK.get(),
                ModConfig.PLAYER_SPEED_FACTOR_STRONG.get(),
                speedFactor,
            )
        val launchSpeed = linearInterpolate(
            ModConfig.THROW_SPEED_WEAK.get(),
            ModConfig.THROW_SPEED_STRONG.get(),
            speedFactor,
        )

        var position = player.eyePosition
        var velocity = player.lookAngle.normalize().scale(launchSpeed).add(player.deltaMovement.scale(playerSpeedFactor))

        val pathPoints = mutableListOf<Vec3>()
        pathPoints.add(position)

        for (i in 0 until SIMULATION_TICKS) {
            val nextPosition = position.add(velocity)
            val clipResult = mc.level?.clip(
                ClipContext(position, nextPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player),
            )
            clipResult?.let { pathPoints.add(it.location) }
            if (clipResult?.type != net.minecraft.world.phys.HitResult.Type.MISS) {
                break
            }
            position = nextPosition
            velocity = velocity.add(0.0, -GRAVITY.toDouble(), 0.0)
        }

        // --- Start Rendering (using lineStrip) ---
        poseStack.pushPose()
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

        // Set up RenderSystem state for translucent line drawing
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader(GameRenderer::getPositionColorShader)
        RenderSystem.disableDepthTest()
        RenderSystem.lineWidth(2.0f)

        val bufferSource = mc.renderBuffers().bufferSource()
        // Use lineStrip() which is designed for continuous paths
        val vertexConsumer = bufferSource.getBuffer(RenderType.lineStrip())

        val matrix = poseStack.last().pose()
        for (point in pathPoints) {
            vertexConsumer.vertex(matrix, point.x.toFloat(), point.y.toFloat(), point.z.toFloat())
                .color(1.0f, 1.0f, 1.0f, 0.8f)
                .normal(0.0f, 1.0f, 0.0f) // THE MISSING PIECE!
                .endVertex()
        }

        // End the batch for lineStrip()
        bufferSource.endBatch(RenderType.lineStrip())

        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()
        poseStack.popPose()
    }
}
