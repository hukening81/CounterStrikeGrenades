package club.pisquad.minecraft.csgrenades.grenades.firegrenade.renderer

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireRegionEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation

class FireRegionDebugRenderer(context: EntityRendererProvider.Context) : EntityRenderer<FireRegionEntity>(context) {
    override fun getTextureLocation(pEntity: FireRegionEntity): ResourceLocation {
        return ResourceLocation(CounterStrikeGrenades.ID, "dummy")
    }

    override fun render(
        entity: FireRegionEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
//        if (!entity.hasInitialized || !entity.debugMode) {
        if (!entity.hasInitialized) {
            return
        }
        poseStack.pushPose()
        val center = entity.position()
        poseStack.translate(-center.x, -center.y, -center.z)

        val buffer = bufferSource.getBuffer(RenderType.lines())
        LevelRenderer.renderLineBox(poseStack, buffer, entity.flameMap.boundingBox, 1f, 0f, 0f, 1f)

        entity.flameMap.keys.forEach {
            val buffer = bufferSource.getBuffer(RenderType.lines())
            LevelRenderer.renderLineBox(poseStack, buffer, it.boundibgBox, 1f, 1f, 1f, 1f)
        }
        poseStack.popPose()
    }
}