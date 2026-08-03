package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.renderer

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeRegionEntity
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.VoxelDebugMode
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelPos
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.AABB

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
        if (entity.debugMode == VoxelDebugMode.NONE) {
            return
        }
        poseStack.pushPose()

        val level = entity.level()
        poseStack.translate(
            -entity.x,
            -entity.y,
            -entity.z,
        )

        this.renderBoundingBox(poseStack, bufferSource, entity.boundingBox)

        when (entity.debugMode) {
            VoxelDebugMode.NONE -> {}
            VoxelDebugMode.ALL -> {
                this.renderVoxels(poseStack, bufferSource, entity.voxelMap.keys)
            }

            VoxelDebugMode.EDGES -> {
                this.renderVoxels(poseStack, bufferSource, entity.voxelMap.edges)
            }

            VoxelDebugMode.SPECIALS -> {
                this.renderVoxels(poseStack, bufferSource, entity.voxelMap.specials)
            }
        }
        poseStack.popPose()
    }

    private fun renderBoundingBox(poseStack: PoseStack, bufferSource: MultiBufferSource, boundingBox: AABB) {
        val buffer = bufferSource.getBuffer(RenderType.lines())
        LevelRenderer.renderLineBox(poseStack, buffer, boundingBox, 0f, 0f, 1f, 1f)
    }

    private fun renderVoxels(poseStack: PoseStack, bufferSource: MultiBufferSource, voxels: Iterable<VoxelPos>) {
        voxels.forEach {
            val buffer = bufferSource.getBuffer(RenderType.lines())
            LevelRenderer.renderLineBox(poseStack, buffer, it.boundibgBox, 1f, 1f, 1f, 1f)
        }
    }
}