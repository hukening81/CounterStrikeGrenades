package club.pisquad.minecraft.csgrenades.config.sections

import club.pisquad.minecraft.csgrenades.config.ConfigSection
import club.pisquad.minecraft.csgrenades.config.common.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.GrenadeType
import net.minecraftforge.common.ForgeConfigSpec

object DecoyConfig : ConfigSection {
    val grenadeCommonConfig = GrenadeCommonConfig()
    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.push(GrenadeType.DECOY.resourceKey)
        grenadeCommonConfig.build(builder)
        builder.pop()
    }
}
