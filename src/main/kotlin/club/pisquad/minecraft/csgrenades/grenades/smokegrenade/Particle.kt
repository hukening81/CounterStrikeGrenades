package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.registry.ModParticleFactories
import club.pisquad.minecraft.csgrenades.registry.ModParticles
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.core.particles.SimpleParticleType

private const val T_PATCILE_KEY = "smoke_particle_t"
private const val CT_PATCILE_KEY = "smoke_particle_ct"

object SmokeParticleRegistry {
    val SMOKE_PARTICLE_T = ModParticles.PARTICLE_TYPES.register(T_SMOKE_RESOURCE_KEY) { SimpleParticleType(true) }
    val SMOKE_PARTICLE_CT = ModParticles.PARTICLE_TYPES.register(CT_SMOKE_RESOURCE_KEY) { SimpleParticleType(true) }

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
    xSpeed: Double,
    ySpeed: Double,
    zSpeed: Double,
) : TextureSheetParticle(level, x, y, z, xSpeed, ySpeed, zSpeed) {
    private var opacityTime: Int = 0

    init {
        this.gravity = 0f
        this.setParticleSpeed(0.0, 0.0, 0.0)
        this.lifetime = 200
        this.scale(4f)
    }

    override fun tick() {
        super.tick()
        if (this.opacityTime > 0) {
            this.alpha = 0f
            this.opacityTime--
        } else {
            this.alpha = 1f
        }
    }

    override fun getRenderType(): ParticleRenderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT

    override fun render(pBuffer: VertexConsumer, pRenderInfo: Camera, pPartialTicks: Float) {
        super.render(pBuffer, pRenderInfo, pPartialTicks)
    }
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
        val particle = SmokeGrenadeParticle(level, x, y, z, 0.0, 0.0, 0.0)
        particle.pickSprite(this.spriteSet)
        return particle
    }
}