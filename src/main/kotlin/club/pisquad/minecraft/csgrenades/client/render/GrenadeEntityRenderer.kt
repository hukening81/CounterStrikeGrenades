package club.pisquad.minecraft.csgrenades.client.render

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModSettings
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.registry.ModEntityModels
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf

//@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
//object GrenadeRenderCacheHelper {
//    data class RenderCache(
//        val grenadeId: Int,
//        val nodes: List<Pair<Double, Vec3>>,
//    ) {
//        companion object {
//            fun create(grenade: CounterStrikeGrenadeEntity): RenderCache {
//                val trajectory = grenade.trajectory
//                val nodes = trajectory.nodesBetweenTick(trajectory.currentTick - 1.toDouble(), trajectory.currentTick.toDouble())
//                return RenderCache(grenade.id, nodes.map { Pair(it.tick - trajectory.currentTick + 1, it.position) })
//            }
//        }
//    }
//
//    @OptIn(ExperimentalAtomicApi::class)
//    val cache: AtomicReference<Map<Int, RenderCache>> = AtomicReference(mapOf())
//
//    @OptIn(ExperimentalAtomicApi::class)
//    @JvmStatic
//    @SubscribeEvent
//    fun onLevelTick(event: TickEvent.PlayerTickEvent) {
//        if (event.side == LogicalSide.SERVER) return
//        if (event.phase == TickEvent.Phase.START) {
//            return
//        }
//
//        val radius = Minecraft.getInstance().options.renderDistance().get().times(16)
//        val player = Minecraft.getInstance().player!!
//        val level = player.level() as ClientLevel
//
//        val entities = level.getEntitiesOfClass(CounterStrikeGrenadeEntity::class.java, player.boundingBox.inflate(radius.toDouble()))
//        cache.store(entities.associate { Pair(it.id, RenderCache.create(it)) })
//    }
//
//    @OptIn(ExperimentalAtomicApi::class)
//    fun get(id: Int): RenderCache? {
//        return cache.load().getOrDefault(id, null)
//    }
//}

class GrenadeEntityRenderer<T>(
    context: EntityRendererProvider.Context,
) : EntityRenderer<T>(context) where T : CounterStrikeGrenadeEntity {

    private val itemRenderer: ItemRenderer = context.itemRenderer

    override fun render(
        entity: T,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSouce: MultiBufferSource,
        packedLight: Int
    ) {
        entity as CounterStrikeGrenadeEntity
        poseStack.pushPose()

        // Movement
        val d = entity.deltaMovement
        val x = Mth.lerp(partialTick.toDouble(), 0.0, d.x)
        var y = Mth.lerp(partialTick.toDouble(), 0.0, d.y)
        y += ModSettings.Entity.GRENADE_ENTITY_SIZE_HALF
        val z = Mth.lerp(partialTick.toDouble(), 0.0, d.z)
        poseStack.translate(x, y, z)

        // Rotation

        poseStack.mulPose(Quaternionf(entity.rotation.getPartialTick(partialTick.toDouble())))

        poseStack.scale(0.4f, 0.4f, 0.4f)

        val itemStack = getItemStack(entity.grenadeType)

        itemRenderer.renderStatic(
            itemStack,
            ItemDisplayContext.FIXED,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            bufferSouce,
            entity.level(),
            entity.id,
        )
        poseStack.popPose()
    }

    override fun getTextureLocation(entity: T): ResourceLocation {
        entity as CounterStrikeGrenadeEntity
        return ModEntityModels.Textures.getTexture(entity.grenadeType)
    }

    private fun getItemStack(grenadeType: GrenadeType): ItemStack {
        return ItemStack(grenadeType.implementation.getItem().get())
    }
}
