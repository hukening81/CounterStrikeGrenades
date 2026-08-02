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
    @Volatile
    private var hasRegistered: Boolean = false

    private val registerEntityRendererTasks: MutableSet<() -> Unit> = mutableSetOf()

    fun addDefferedRegisterEntityRendererTask(task: () -> Unit) {
        this.registerEntityRendererTasks.add(task)
    }

    @JvmStatic
    @SubscribeEvent
    @Suppress("unused")
    fun registerEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        hasRegistered = true

        this.registerEntityRendererTasks.forEach {
            it()
        }

        GrenadeType.entries.forEach {
            EntityRenderers.register(it.properties.entity.get(), ::GrenadeEntityRenderer)
        }

    }
}
