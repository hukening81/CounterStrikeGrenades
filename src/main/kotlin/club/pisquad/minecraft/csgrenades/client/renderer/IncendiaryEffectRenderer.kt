package club.pisquad.minecraft.csgrenades.client.renderer

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.entity.AbstractFireGrenade
import club.pisquad.minecraft.csgrenades.entity.CounterStrikeGrenadeEntity.Companion.isExplodedAccessor
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec2
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object IncendiaryEffectRenderer {
    private val randomSource = RandomSource.createNewThreadLocalInstance()

    @SubscribeEvent
    fun tick(event: TickEvent.RenderTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            // This may have some performance issues, but it depends on how Minecraft lookup entities
            val minecraft = Minecraft.getInstance()
            val player = minecraft.player ?: return
            val renderDistance = minecraft.options.renderDistance().get()
            val incendiaries = player.level()
                .getEntitiesOfClass(
                    AbstractFireGrenade::class.java,
                    player.boundingBox.inflate(renderDistance.toDouble() * 16)
                ) { it.entityData.get(isExplodedAccessor) }
            for (incendiary in incendiaries) {
                renderOneIncendiary(incendiary)
            }
        }
    }

    private fun renderOneIncendiary(incendiary: AbstractFireGrenade) {
        val particleEngine = Minecraft.getInstance().particleEngine
        val spreadBlocks = incendiary.entityData.get(AbstractFireGrenade.spreadBlocksAccessor) ?: return
        val particlePerTick =
            (FIREGRENADE_RANGE * FIREGRENADE_RANGE * INCENDIARY_PARTICLE_DENSITY) * spreadBlocks.size / (FIREGRENADE_RANGE * FIREGRENADE_RANGE)

        for (i in 0 until particlePerTick) {
            val position = incendiary.position()


            val pos = getRandomLocationFromBlockSurface(spreadBlocks.random())
            if (isPositionInSmoke(
                    position,
                    SMOKE_GRENADE_RADIUS.toDouble() - 0.5
                )
            ) {
                continue
            }

            val distance = Vec2(
                position.x.minus(pos.x).toFloat(),
                position.z.minus(pos.z).toFloat()
            ).length().toDouble()

            val random = randomSource.nextDouble()
            val particleType = when {
                random < 0.2 -> ParticleTypes.SMOKE
                random < 0.5 -> ParticleTypes.FLAME
                else -> ParticleTypes.SMALL_FLAME
            }


            particleEngine.createParticle(
                particleType,
                pos.x,
                pos.y,
                pos.z,
                0.0,
                0.1,
                0.0
            )?.lifetime = getLifetimeFromDistance(distance)
        }

    }
}

fun getLifetimeFromDistance(distance: Double): Int {
    val randomSource = RandomSource.createNewThreadLocalInstance()
    return (FIREGRENADE_RANGE - distance).div(FIREGRENADE_LIFETIME).times(INCENDIARY_PARTICLE_LIFETIME)
        .toInt() + randomSource.nextInt(0, 5)
}