package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeParticle
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeRegionEntity
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.util.Mth
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
            context.packetHandled = true
            context.enqueueWork {
                val level = Minecraft.getInstance().level?:return@enqueueWork
                val entity = level.getEntity(msg.entityID)?:return@enqueueWork
                if (entity !is SmokeRegionEntity) {
                    return@enqueueWork
                }
                entity.applyPatch(msg.patch)
        }
    }
    }
}

@Serializable
sealed interface SmokePatch {
    fun apply(particles: Iterable<SmokeGrenadeParticle>)

    @Serializable
    class Projectile(
        @Serializable(with = Vec3Serializer::class) val origin: Vec3,
        @Serializable(with = Vec3Serializer::class) val destination: Vec3,
        val radius: Double
    ) : SmokePatch {
        override fun apply(particles: Iterable<SmokeGrenadeParticle>) {
            val axis = this.destination.subtract(this.origin)
            particles.forEach {
                val t = it.position.subtract(this.origin).dot(axis) / axis.lengthSqr()
                if (t < 0.0 || t > 1.0) {
                    return@forEach
                }
                val p = this.origin.add(axis.scale(t))
                val d = p.distanceTo(it.position)

                if (d > this.radius) {
                    return@forEach
                }

                val hideTime = Mth.lerp(d / this.radius, 20.0, 10.0).toInt()
                it.hide(hideTime)
            }
        }
    }

    @Serializable
    class Explosion(
        @Serializable(with = Vec3Serializer::class) val center: Vec3,
        val radius: Double,
    ) : SmokePatch {
        override fun apply(particles: Iterable<SmokeGrenadeParticle>) {
            particles.forEach {
                val distance = it.position.distanceTo(this.center)
                if (distance > this.radius) {
                    return@forEach
                }
                val hideTime = Mth.lerp(distance / this.radius, 40.0, 10.0).toInt()
                it.hide(hideTime)
            }
        }
    }
}