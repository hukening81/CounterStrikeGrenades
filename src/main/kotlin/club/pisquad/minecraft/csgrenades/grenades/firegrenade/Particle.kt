package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.registry.ModParticleFactories
import club.pisquad.minecraft.csgrenades.registry.ModParticles
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.core.particles.SimpleParticleType

private const val INCENDIARY_FLAME_PARTICLE_KEY = "incendiary_flame"
private const val MOLOTOV_FLAME_PARTICLE_KEY = "molotov_flame"

object FlameParticleRegistry {
    val INCENDIARY_FLAME =
        ModParticles.PARTICLE_TYPES.register(INCENDIARY_FLAME_PARTICLE_KEY) { SimpleParticleType(true) }
    val MOLOTOV_FLAME = ModParticles.PARTICLE_TYPES.register(MOLOTOV_FLAME_PARTICLE_KEY) { SimpleParticleType(true) }

    init {
        ModParticleFactories.addRegisterTask {
            it.registerSpriteSet(this.INCENDIARY_FLAME.get()) { spriteSet ->
                FlameParticleFactory(
                    spriteSet
                )
            }
            it.registerSpriteSet(this.MOLOTOV_FLAME.get()) { spriteSet ->
                FlameParticleFactory(
                    spriteSet
                )
            }
        }
    }
}

class FlameParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    xSpeed: Double,
    val ySpeed: Double,
    zSpeed: Double,
) : TextureSheetParticle(level, x, y, z, xSpeed, ySpeed, zSpeed) {

    override fun getRenderType(): ParticleRenderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    override fun tick() {
        this.age++
        if (this.age > this.lifetime) {
            this.remove()
        }
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z

        this.y = this.y + this.ySpeed
    }
}

class FlameParticleFactory(
    private val spriteSet: SpriteSet,
) : ParticleProvider<SimpleParticleType> {
    override fun createParticle(
        type: SimpleParticleType,
        level: ClientLevel,
        x: Double,
        y: Double,
        z: Double,
        xSpeed: Double,
        ySpeed: Double,
        zSpeed: Double
    ): Particle? {
        val particle = FlameParticle(level, x, y, z, 0.0, ySpeed, 0.0)
        particle.pickSprite(this.spriteSet)
        return particle
    }
}