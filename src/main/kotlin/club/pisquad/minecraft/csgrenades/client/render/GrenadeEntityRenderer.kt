package club.pisquad.minecraft.csgrenades.client.render

import club.pisquad.minecraft.csgrenades.entity.core.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registry.ModEntityModels
import club.pisquad.minecraft.csgrenades.registry.ModItems
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

    //    override fun render(
//        entity: T,
//        entityYaw: Float,
//        partialTicks: Float,
//        poseStack: PoseStack,
//        buffer: MultiBufferSource,
//        packedLight: Int,
//    ) {
//        entity as CounterStrikeGrenadeEntity
//        // Hide fire grenade model after it explodes
//        if (entity is AbstractFireGrenadeEntity && entity.entityData.get(CounterStrikeGrenadeEntity.isActivatedAccessor)) {
//            return
//        }
//        val positionCache = GrenadeRenderCacheHelper.get(entity.id) ?: return
//
//        poseStack.pushPose()
//
////        val itemStack = entity.getItem()
////        if (!itemStack.isEmpty) {
//            // 使用 lerp (线性插值) 来平滑地获取实体在两帧之间的旋转角度
////            val visualYRot = Mth.rotLerp(partialTicks, entity.customYRotO, entity.customYRot)
////            val visualXRot = Mth.rotLerp(partialTicks, entity.customXRotO, entity.customXRot)
////            val visualZRot = Mth.rotLerp(partialTicks, entity.customZRotO, entity.customZRot)
//
//            // Translate the model to align its visual center with its physical center
//            poseStack.translate(0.0, 0.125, 0.0)
//
//            // Smooth movement between ticks
//
//
//
//            // get precise position
//
//            // 关键：应用实体自身的三轴旋转
////            poseStack.mulPose(Axis.YP.rotationDegrees(visualYRot))
////            poseStack.mulPose(Axis.ZP.rotationDegrees(visualZRot)) // 使用Z轴
////            poseStack.mulPose(Axis.XP.rotationDegrees(visualXRot))
//
//            // 修正模型大小
//            poseStack.scale(0.5f, 0.5f, 0.5f)
//
//            // --- START MODIFICATION ---
////            val itemName = itemStack.item.descriptionId.replaceFirst("item.csgrenades.", "")
//            // Model path for items is usually "models/item/[item_name].json"
//            // So for a thrown item, it would be "models/item/[item_name]_t.json"
//            val resourceKey = entity.grenadeType.resourceKey
//            val thrownModelLocation = ResourceLocation("csgrenades", "item/${resourceKey}_t")
//
//            val modelManager = Minecraft.getInstance().itemRenderer.itemModelShaper.modelManager
//            val bakedModel: BakedModel? = modelManager.getModel(thrownModelLocation)
//
////            if (bakedModel != null && bakedModel != modelManager.missingModel && !bakedModel.isCustomRenderer) {
////                itemRenderer.render(
////                    itemStack,
////                    ItemDisplayContext.FIXED,
////                    false, // left-handed. Doesn't matter much for grenades unless there's specific rendering logic based on this
////                    poseStack,
////                    buffer,
////                    packedLight,
////                    OverlayTexture.NO_OVERLAY,
////                    bakedModel,
////                )
////            } else {
////                // Fallback to original rendering if custom model not found or is a custom renderer
////                itemRenderer.renderStatic(
////                    itemStack,
////                    ItemDisplayContext.FIXED,
////                    packedLight,
////                    OverlayTexture.NO_OVERLAY,
////                    poseStack,
////                    buffer,
////                    entity.level(),
////                    entity.id,
////                )
////            }
////            // --- END MODIFICATION ---
////        }
//
//        poseStack.popPose()
//        // super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight) // 移除super调用以消除阴影和“扭头”效果
//    }
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
        val d = entity.deltaMovement


        val x = Mth.lerp(partialTick.toDouble(), 0.0, d.x)
        val y = Mth.lerp(partialTick.toDouble(), 0.0, d.y)
        val z = Mth.lerp(partialTick.toDouble(), 0.0, d.z)

        poseStack.translate(x, y, z)

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
        return when (grenadeType) {
            GrenadeType.FLASH_BANG -> ItemStack(ModItems.FLASH_BANG_ITEM.get())
            GrenadeType.SMOKE_GRENADE -> ItemStack(ModItems.SMOKE_GRENADE_ITEM.get())
            GrenadeType.HE_GRENADE -> ItemStack(ModItems.HEGRENADE_ITEM.get())
            GrenadeType.INCENDIARY -> ItemStack(ModItems.INCENDIARY_ITEM.get())
            GrenadeType.MOLOTOV -> ItemStack(ModItems.MOLOTOV_ITEM.get())
            GrenadeType.DECOY -> ItemStack(ModItems.DECOY_GRENADE_ITEM.get())
        }
    }
}
