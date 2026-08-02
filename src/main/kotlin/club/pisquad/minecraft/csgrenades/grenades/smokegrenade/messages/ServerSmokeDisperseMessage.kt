package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeRegionEntity
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

@Serializable
class ServerSmokeDisperseMessage(
    val entityID: Int,
    val patch: SmokePatch
) {
    companion object : CsGrenadeMessageHandler<ServerSmokeDisperseMessage>(ServerSmokeDisperseMessage::class) {
        override fun handler(
            msg: ServerSmokeDisperseMessage,
            ctx: Supplier<NetworkEvent.Context>
        ) {
            val context = ctx.get()
            context.enqueueWork {
                val level = Minecraft.getInstance().level?:return@enqueueWork
                val entity = level.getEntity(msg.entityID)?:return@enqueueWork
                if (entity !is SmokeRegionEntity) {
                    return@enqueueWork
                }
                entity.applyPatch(msg.patch)

            context.packetHandled = true
        }
    }
    }
}

@Serializable
sealed interface SmokePatch {
    fun apply(particles: Iterable<Particle>)

    @Serializable
    class Projectile(
        @Serializable(with = Vec3Serializer::class) val origin: Vec3,
        @Serializable(with = Vec3Serializer::class) val direction: Vec3,
        val radius: Double
    ) : SmokePatch {
        override fun apply(particles: Iterable<Particle>) {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    class Explosion(
        @Serializable(with = Vec3Serializer::class) val center: Vec3,
        val radius: Double,
    ) : SmokePatch {
        override fun apply(particles: Iterable<Particle>) {
            TODO("Not yet implemented")
        }
    }
}