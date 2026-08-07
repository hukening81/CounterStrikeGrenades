package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.config.ConfigBuilder
import net.minecraftforge.common.ForgeConfigSpec

class FireGrenadeCommonConfig(
) : ConfigBuilder {
    lateinit var spreadRadius: ForgeConfigSpec.DoubleValue
    lateinit var maxDamagePerTick: ForgeConfigSpec.DoubleValue
    lateinit var fireHeight: ForgeConfigSpec.DoubleValue
    lateinit var damage: ForgeConfigSpec.DoubleValue
    lateinit var damageTransitionTime: ForgeConfigSpec.DoubleValue

    override fun build(builder: ForgeConfigSpec.Builder) {
        this.spreadRadius = builder.defineInRange("spread_radius", 3.0, 1.0, 10.0)
        this.fireHeight = builder.defineInRange("max_damage_per_tick", 2.0, 0.0, 10.0)
        this.fireHeight = builder.defineInRange("fire_height", 2.5, 0.0, 10.0)
        this.damage = builder.defineInRange("damage", 2.0, 0.0, 100.0)
        this.damageTransitionTime = builder.defineInRange("damage_transition_time", 1.0, 0.0, 10.0)
    }
}
