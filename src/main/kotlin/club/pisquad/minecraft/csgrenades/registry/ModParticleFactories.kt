package club.pisquad.minecraft.csgrenades.registry

//import club.pisquad.minecraft.csgrenades.particle.SmokeParticleFactory
import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterParticleProvidersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod


@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ModParticleFactories {
    @JvmStatic
    @SubscribeEvent
    fun onSetup(event: RegisterParticleProvidersEvent) {
//        event.registerSpriteSet(ModParticles.SMOKE_PARTICLE.get()) { sprite: SpriteSet -> SmokeParticleFactory(sprite) }
    }
}
