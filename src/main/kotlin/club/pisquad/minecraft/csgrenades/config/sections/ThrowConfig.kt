package club.pisquad.minecraft.csgrenades.config.sections

import club.pisquad.minecraft.csgrenades.config.ConfigSection
import net.minecraftforge.common.ForgeConfigSpec

object ThrowConfig : ConfigSection {
    lateinit var cooldown: ForgeConfigSpec.DoubleValue
    lateinit var speed_weak: ForgeConfigSpec.DoubleValue
    lateinit var speed_medium: ForgeConfigSpec.DoubleValue
    lateinit var speed_strong: ForgeConfigSpec.DoubleValue
    lateinit var strength_transition_time: ForgeConfigSpec.DoubleValue
    lateinit var fov_effect_amount: ForgeConfigSpec.DoubleValue
    lateinit var trajectory_color: ForgeConfigSpec.ConfigValue<String>

    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.push("throw")
        cooldown = builder.defineInRange("cooldown", 1.0, 0.1, 600.0)
        speed_weak = builder.defineInRange("throw_speed_weak", 15.0, 1.0, 100.0)
        speed_medium = builder.defineInRange("throw_speed_medium", 20.0, 1.0, 100.0)
        speed_strong = builder.defineInRange("throw_speed_strong", 25.0, 1.0, 100.0)
        strength_transition_time = builder.defineInRange("strength_transition_time", 1.0, 0.1, 10.0)
        fov_effect_amount = builder.defineInRange("fov_effect_amount", 0.12, 0.0, 1.0)
        trajectory_color = builder.define("trajectory_preview_color", "#FFFFFF")
        builder.pop()
    }
}
