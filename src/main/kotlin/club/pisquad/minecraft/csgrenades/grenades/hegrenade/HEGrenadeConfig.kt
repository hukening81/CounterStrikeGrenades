package club.pisquad.minecraft.csgrenades.grenades.hegrenade

import club.pisquad.minecraft.csgrenades.config.ConfigBuilder
import club.pisquad.minecraft.csgrenades.config.GrenadeCommonConfig
import net.minecraftforge.common.ForgeConfigSpec

object HEGrenadeConfig : ConfigBuilder {
    val common = GrenadeCommonConfig(2.5)
    val explosion = HEGrenadeExplosionConfig
    override fun build(builder: ForgeConfigSpec.Builder) {
        common.build(builder)

        builder.push("explosion")
        explosion.build(builder)
        builder.pop()
    }
}

object HEGrenadeExplosionConfig : ConfigBuilder {
    lateinit var radius: ForgeConfigSpec.DoubleValue
    lateinit var smokeClearRadius: ForgeConfigSpec.DoubleValue
    lateinit var damageAmount: ForgeConfigSpec.DoubleValue
    lateinit var headDamageMultiplier: ForgeConfigSpec.DoubleValue

    override fun build(builder: ForgeConfigSpec.Builder) {
        radius = builder.defineInRange("radius", 5.0, 0.0, 10.0)
        smokeClearRadius = builder.defineInRange("smoke_clear_radius", 3.0, 0.0, 10.0)

        damageAmount = builder.defineInRange("damage_amount", 10.0, 0.1, 100.0)
        headDamageMultiplier = builder.defineInRange("head_damage_multiplier", 1.5, 0.1, 10.0)
    }
}