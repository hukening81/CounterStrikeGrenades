package club.pisquad.minecraft.csgrenades.config.sections

import club.pisquad.minecraft.csgrenades.config.ConfigSection
import club.pisquad.minecraft.csgrenades.config.common.FireGrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.config.common.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.GrenadeType
import net.minecraftforge.common.ForgeConfigSpec

object MolotovConfig : ConfigSection {
    val grenadeCommonConfig = GrenadeCommonConfig(2.5)
    val fireGrenadeCommonConfig = FireGrenadeCommonConfig()

    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.push(GrenadeType.MOLOTOV.resourceKey)
        grenadeCommonConfig.build(builder)
        fireGrenadeCommonConfig.build(builder)
        builder.pop()
    }
}
