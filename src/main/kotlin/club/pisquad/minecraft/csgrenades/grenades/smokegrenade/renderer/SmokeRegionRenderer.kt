package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.renderer

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeRegionEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation

//object SmokeRegionRenderer {
//    @JvmStatic
//    @SubscribeEvent
//    fun onRender(event: RenderLevelStageEvent) {
//        val minecraft = Minecraft.getInstance()
//        val player = minecraft.player!!
//        val level = player.level()
//        val renderDistanceMeter = minecraft.options.renderDistance().get().times(16)
//        val activatedSmokes = level.getEntitiesOfClass(
//            SmokeGrenadeEntity::class.java,
//            player.boundingBox.inflate(renderDistanceMeter.toDouble())
//        ) {
//            it.isActivated()
//        }
//        activatedSmokes.forEach { smoke ->
//            smoke.getSmokeDataPoints().forEach { point ->
//                run {
//                    minecraft.particleEngine.createParticle(
//                        ParticleTypes.ASH,
//                        point.position.x.toDouble(),
//                        point.position.y.toDouble(),
//                        point.position.z.toDouble(),
//                        0.0,
//                        0.0,
//                        0.0,
//                    )?.scale(4f)?.lifetime = 1
//                }
//            }
//        }
//    }
//}
class SmokeRegionRenderer(context: EntityRendererProvider.Context) : EntityRenderer<SmokeRegionEntity>(context) {
    override fun getTextureLocation(pEntity: SmokeRegionEntity): ResourceLocation {
        return ResourceLocation(CounterStrikeGrenades.ID, "dummy")
    }

    override fun render(
        entity: SmokeRegionEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()

        val level = entity.level()

        val box = entity.boundingBox.move(
            -entity.x,
            -entity.y,
            -entity.z,
        )

        val buffer = bufferSource.getBuffer(RenderType.lines())
        LevelRenderer.renderLineBox(poseStack, buffer, box, 1f, 1f, 1f, 1f)

        poseStack.popPose()
    }

}