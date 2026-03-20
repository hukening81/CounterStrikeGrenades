package club.pisquad.minecraft.csgrenades.config.sections

import club.pisquad.minecraft.csgrenades.config.ConfigSection
import club.pisquad.minecraft.csgrenades.config.common.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import net.minecraftforge.common.ForgeConfigSpec

object SmokeGrenadeConfig : ConfigSection {
    lateinit var smokeRadius: ForgeConfigSpec.DoubleValue
    val grenadeCommonConfig = GrenadeCommonConfig(2.5)
    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.push(GrenadeType.SMOKE_GRENADE.resourceKey)

        grenadeCommonConfig.build(builder)

        smokeRadius = builder.defineInRange("smoke_radius", 6.0, 1.0, 20.0)

        builder.pop()

    }


}
