package club.pisquad.minecraft.csgrenades.client.render.hegrenade

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.getRandomLocationFromSphere
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.HEGrenadeRegistries
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundSource
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.random.Random

data class HEGrenadeExplosionData(
    var position: Vec3,
)

// Effect rendering should not depends on the grenade entity itself since the entity is discarded when activate
// Since we use particles for rendering he grenade, following logic all happen in the main thread
@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object HEGrenadeRenderManager {
    private val renderers: MutableList<HEGrenadeRenderer> = mutableListOf()

    fun render(data: HEGrenadeExplosionData) {
        renderers.add(HEGrenadeRenderer(data))
    }

    @JvmStatic
    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END) return

        val shouldRemove: MutableList<HEGrenadeRenderer> = mutableListOf()
        renderers.forEach {
            if (it.update()) {
                shouldRemove.add(it)
            }
        }
        shouldRemove.forEach {
            renderers.remove(it)
        }
    }
}

class HEGrenadeRenderer(
    private val data: HEGrenadeExplosionData,
) {
    var done: Boolean = false
    private var soundInstance: SoundInstance? = null

    init {
        val player = Minecraft.getInstance().player!!
        val distance = player.position().distanceTo(data.position)

        val soundEvent =
            if (distance > 15) HEGrenadeRegistries.sounds.explodeDistant.getSoundEvent() else HEGrenadeRegistries.sounds.explode.getSoundEvent()
        val volume: Float =
            if (distance > 15) 3.0f else 0.5f


        player.level().playLocalSound(
            data.position.x,
            data.position.y,
            data.position.z,
            soundEvent,
            SoundSource.PLAYERS,
            volume,
            1.0f,
            false
        )

        val particleEngine = Minecraft.getInstance().particleEngine

        for (i in 1..500) {
            particleEngine.createParticle(
                ParticleTypes.SMOKE,
                data.position.x,
                data.position.y,
                data.position.z,
                Random.nextDouble().times(1.4) - 0.7,
                Random.nextDouble().times(1.4) - 0.7,
                Random.nextDouble().times(1.4) - 0.7,
            )?.lifetime = 10
        }
        for (i in 1..100) {
            val location = getRandomLocationFromSphere(data.position, 4.0)
            particleEngine.createParticle(
                ParticleTypes.LARGE_SMOKE,
                location.x,
                location.y,
                location.z,
                0.0,
                0.0,
                0.0,
            )?.scale(1.5f)?.lifetime = 20
        }
    }

    fun update(): Boolean = true
}
