package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod


@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ModParticleFactories {

    private var hasRegistered = false

    private val registerTasks: MutableSet<(RegisterParticleProvidersEvent) -> Unit> = mutableSetOf()

    fun addRegisterTask(task: (RegisterParticleProvidersEvent) -> Unit) {
        this.registerTasks.add(task)
    }

    @JvmStatic
    @SubscribeEvent
    fun onSetup(event: RegisterParticleProvidersEvent) {
        this.hasRegistered = true
        this.registerTasks.forEach {
            it(event)
        }
    }
}
