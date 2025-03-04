package club.pisquad.minecraft.csgrenades.particle

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.millToTick
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.core.particles.SimpleParticleType

class SmokeGrenadeParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    xSpeed: Double,
    ySpeed: Double,
    zSpeed: Double
) : TextureSheetParticle(level, x, y, z, xSpeed, ySpeed, zSpeed) {
    var opacityTime: Int = 0

    init {
        this.gravity = 0f
        this.setParticleSpeed(0.0, 0.0, 0.0)
        this.lifetime = (ModConfig.SmokeGrenade.SMOKE_LIFETIME.get().millToTick()).toInt()
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

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    override fun render(pBuffer: VertexConsumer, pRenderInfo: Camera, pPartialTicks: Float) {
        super.render(pBuffer, pRenderInfo, pPartialTicks)
    }
}

class SmokeParticleFactory(
    private val spriteSet: SpriteSet
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
    ): SmokeGrenadeParticle {
        val particle = SmokeGrenadeParticle(level, x, y, z, 0.0, 0.0, 0.0)
        particle.pickSprite(spriteSet)
        return particle
    }
}