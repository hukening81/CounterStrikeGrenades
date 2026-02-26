package club.pisquad.minecraft.csgrenades.config.sections

import club.pisquad.minecraft.csgrenades.config.ConfigSection
import net.minecraftforge.common.ForgeConfigSpec

object ThrowConfig : ConfigSection {
    lateinit var COOLDOWN: ForgeConfigSpec.DoubleValue
    lateinit var SPEED_WEAK: ForgeConfigSpec.DoubleValue
    lateinit var SPEED_MEDIUM: ForgeConfigSpec.DoubleValue
    lateinit var SPEED_STRONG: ForgeConfigSpec.DoubleValue
    lateinit var STRENGTH_TRANSITION_TIME: ForgeConfigSpec.DoubleValue
    lateinit var FOV_EFFECT_AMOUNT: ForgeConfigSpec.DoubleValue
    lateinit var TRAJECTORY_COLOR: ForgeConfigSpec.ConfigValue<String>

    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.push("throw")
        COOLDOWN = builder.defineInRange("cooldown", 1.0, 0.1, 600.0)
        SPEED_WEAK = builder.defineInRange("throw_speed_weak", 15.0, 1.0, 100.0)
        SPEED_MEDIUM = builder.defineInRange("throw_speed_medium", 20.0, 1.0, 100.0)
        SPEED_STRONG = builder.defineInRange("throw_speed_strong", 25.0, 1.0, 100.0)
        STRENGTH_TRANSITION_TIME = builder.defineInRange("strength_transition_time", 1.0, 0.1, 10.0)
        FOV_EFFECT_AMOUNT = builder.defineInRange("fov_effect_amount", 0.12, 0.0, 1.0)
        TRAJECTORY_COLOR = builder.define("trajectory_preview_color", "#FFFFFF")
        builder.pop()
    }
}
