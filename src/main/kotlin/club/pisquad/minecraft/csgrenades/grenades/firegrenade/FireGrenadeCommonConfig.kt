package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.config.ConfigSection
import net.minecraftforge.common.ForgeConfigSpec

class FireGrenadeCommonConfig(
    val defaultSpreadRadius: Double = 3.0,
    val defaultMaxDamagePerTick: Double = 2.0,
) : ConfigSection {
    lateinit var spreadRadius: ForgeConfigSpec.DoubleValue
    lateinit var maxDamagePerTick: ForgeConfigSpec.DoubleValue
    lateinit var fireHeight: ForgeConfigSpec.DoubleValue

    override fun build(builder: ForgeConfigSpec.Builder) {
        this.spreadRadius = builder.defineInRange("spread_radius", defaultSpreadRadius, 1.0, 10.0)
        this.fireHeight = builder.defineInRange("max_damage_per_tick", defaultMaxDamagePerTick, 0.0, 10.0)
        this.fireHeight = builder.defineInRange("fire_height", 2.5, 0.0, 10.0)
    }
}