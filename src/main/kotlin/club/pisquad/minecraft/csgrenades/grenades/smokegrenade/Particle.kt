package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.registry.ModParticleFactories
import club.pisquad.minecraft.csgrenades.registry.ModParticles
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.world.phys.Vec3
import kotlin.math.max

private const val T_PARTICLE_KEY = "smoke_particle_t"
private const val CT_PARTICLE_KEY = "smoke_particle_ct"

object SmokeParticleRegistry {
    val SMOKE_PARTICLE_T = ModParticles.PARTICLE_TYPES.register(T_PARTICLE_KEY) { SimpleParticleType(true) }
    val SMOKE_PARTICLE_CT = ModParticles.PARTICLE_TYPES.register(CT_PARTICLE_KEY) { SimpleParticleType(true) }

    init {
        ModParticleFactories.addRegisterTask {
            it.registerSpriteSet(this.SMOKE_PARTICLE_CT.get()) { spriteSet ->
                SmokeParticleFactory(
                    spriteSet
                )
            }
            it.registerSpriteSet(this.SMOKE_PARTICLE_T.get()) { spriteSet ->
                SmokeParticleFactory(
                    spriteSet
                )
            }
        }
    }
}


class SmokeGrenadeParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
) : TextureSheetParticle(
    level,
    x, y, z, 0.0, 0.0, 0.0
) {
    val position = Vec3(x, y, z)

    @Transient
    var hideTimer: Int = 0

    init {
        this.gravity = 0f
        this.setParticleSpeed(0.0, 0.0, 0.0)
        this.scale(1.5f)
    }

    override fun tick() {
        // We don't need its advanced features
//         super.tick()

        if (this.age++ > this.lifetime) {
            this.remove()
        }

        if (this.hideTimer > 0) {
            this.hideTimer--
            return
        } else if (this.hideTimer == 0) {
            this.alpha = 1f
        }
    }

    fun hide(timeout: Int) {
        this.hideTimer = max(this.hideTimer, timeout)
        this.alpha = 0f
    }

    override fun getRenderType(): ParticleRenderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
}

class SmokeParticleFactory(
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
        zSpeed: Double,
    ): SmokeGrenadeParticle {
        val particle = SmokeGrenadeParticle(level, x, y, z)
        particle.pickSprite(this.spriteSet)
        return particle
    }
}