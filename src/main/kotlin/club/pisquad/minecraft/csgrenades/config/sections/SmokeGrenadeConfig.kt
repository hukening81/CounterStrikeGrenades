package club.pisquad.minecraft.csgrenades.config.sections

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.config.ConfigSection
import club.pisquad.minecraft.csgrenades.config.common.GrenadeCommonConfig
import net.minecraftforge.common.ForgeConfigSpec

object SmokeGrenadeConfig : ConfigSection {
    lateinit var smokeWidth: ForgeConfigSpec.DoubleValue
    lateinit var smokeHeight: ForgeConfigSpec.DoubleValue
    lateinit var maxFall: ForgeConfigSpec.DoubleValue
    lateinit var initialQuantity: ForgeConfigSpec.IntValue
    val grenadeCommonConfig = GrenadeCommonConfig(2.5)
    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.push(GrenadeType.SMOKE_GRENADE.resourceKey)

        grenadeCommonConfig.build(builder)

        smokeWidth = builder.defineInRange("shape_max_width", 6.0, 1.0, 20.0)
        smokeHeight = builder.defineInRange("shape_max_height", 5.0, 1.0, 20.0)
        maxFall = builder.defineInRange("shape_max_fall", 10.0, 1.0, 20.0)
        initialQuantity = builder.defineInRange("initial_quantity", 15, 5, 25)
        builder.pop()
    }


}
