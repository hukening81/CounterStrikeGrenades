package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.client.render.GrenadeEntityRenderer
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod


@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ModRenderers {
    @JvmStatic
    @SubscribeEvent
    fun registerEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        GrenadeType.entries.forEach {
            EntityRenderers.register(it.registries.get().entity.get(), ::GrenadeEntityRenderer)
        }
    }
}
