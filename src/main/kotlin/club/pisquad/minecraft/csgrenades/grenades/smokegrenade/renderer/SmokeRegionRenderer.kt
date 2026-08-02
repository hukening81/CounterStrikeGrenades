package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.renderer

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeRegionEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.AABB
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.Mod

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
        poseStack.translate(
            -entity.x,
            -entity.y,
            -entity.z,
        )

        val buffer = bufferSource.getBuffer(RenderType.lines())
        LevelRenderer.renderLineBox(poseStack, buffer, entity.boundingBox, 0f, 0f, 1f, 1f)
        poseStack.popPose()
    }
}

//
//@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
//object SmokeRegionParticleRenderer {
//
//    @JvmStatic
//    @SubscribeEvent
//    fun onTick(event: TickEvent.LevelTickEvent) {
//        if (event.side != LogicalSide.CLIENT) {
//            return
//        }
//        val center = Minecraft.getInstance().player!!.position()
//        val renderDistance = Minecraft.getInstance().options.renderDistance().get() * 16.0
//        val level = event.level as ClientLevel
//        val smokeRegions = level.getEntitiesOfClass(
//            SmokeRegionEntity::class.java,
//            AABB.ofSize(center, renderDistance, renderDistance, renderDistance)
//        ).forEach {
//            this.renderSingle(it)
//        }
//    }
//
//    fun renderSingle(entity: SmokeRegionEntity) {
//        entity.voxelMap.forEach { pos, voxel ->
//            val center = pos.center
//            val particle = Minecraft.getInstance().particleEngine.createParticle(
//                ParticleTypes.ASH,
//                center.x, center.y, center.z, 0.0, 0.0, 0.0
//            )
//            if (particle != null) {
//                particle.lifetime = 1
//                particle.scale(10f)
//            }
//        }
//    }
//}