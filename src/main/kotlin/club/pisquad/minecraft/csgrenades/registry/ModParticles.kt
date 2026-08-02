package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.ModLogger
import net.minecraft.core.particles.ParticleType
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModParticles {
    val PARTICLE_TYPES: DeferredRegister<ParticleType<*>> =
        DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CounterStrikeGrenades.ID)

//    val SMOKE_PARTICLE: RegistryObject<SimpleParticleType> =
//        PARTICLE_TYPES.register("smoke_particle") { SimpleParticleType(true) }

    fun register(bus: IEventBus) {
        ModLogger.info("Registering particles")
        this.PARTICLE_TYPES.register(bus)
    }
}
