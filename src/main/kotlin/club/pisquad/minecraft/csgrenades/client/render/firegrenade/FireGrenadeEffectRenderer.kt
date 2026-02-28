package club.pisquad.minecraft.csgrenades.client.render.firegrenade

import net.minecraft.util.RandomSource

//fun getLifetimeFromDistance(distance: Double): Int = linearInterpolate(
//    INCENDIARY_PARTICLE_LIFETIME.toDouble(),
//    0.0,
//    distance.div(ModConfig.incendiary.FIRE_RANGE.get()),
//).div(50).toInt()
//

object FireGrenadeRenderer {
    private val randomSource = RandomSource.createNewThreadLocalInstance()

//    fun renderOne(grenade: FireGrenadeEntity) {
//        val particleEngine = Minecraft.getInstance().particleEngine
//        val spreadBlocks = grenade.getSpreadBlocks()
//        if (spreadBlocks.isEmpty()) {
//            return
//        }
//        val fireRange = ModConfig.FireGrenade.FIRE_RANGE.get()
//        val particlePerTick =
//            (fireRange * fireRange * INCENDIARY_PARTICLE_DENSITY) * spreadBlocks.size / (fireRange * fireRange)
//
//        for (i in 0 until particlePerTick) {
//            val position = grenade.position()
//
//            val pos = getRandomLocationFromBlockSurface(spreadBlocks.random())
//            if (isPositionInSmoke(
//                    grenade.level(),
//                    pos.add(Vec3(0.0, 0.5, 0.0)),
//                )
//            ) {
//                continue
//            }
//
//            val distance = Vec2(
//                position.x.minus(pos.x).toFloat(),
//                position.z.minus(pos.z).toFloat(),
//            ).length().toDouble()
//
//            val flameParticleType = when (grenade) {
//                is IncendiaryEntity -> ParticleTypes.SOUL_FIRE_FLAME
//                else -> ParticleTypes.FLAME
//            }
//            val random = randomSource.nextDouble()
//            val particleType = when {
//                random < 0.2 -> ParticleTypes.SMOKE
//                random < 0.5 -> flameParticleType
//                else -> ParticleTypes.SMALL_FLAME
//            }
//            particleEngine.createParticle(
//                particleType,
//                pos.x,
//                pos.y,
//                pos.z,
//                0.0,
//                0.1,
//                0.0,
//            )?.lifetime = getLifetimeFromDistance(distance)
//        }
//    }
}
