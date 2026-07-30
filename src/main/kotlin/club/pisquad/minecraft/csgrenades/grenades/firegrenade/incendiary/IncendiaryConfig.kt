package club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary

import club.pisquad.minecraft.csgrenades.config.ConfigBuilder
import club.pisquad.minecraft.csgrenades.config.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeCommonConfig
import net.minecraftforge.common.ForgeConfigSpec

object IncendiaryConfig : ConfigBuilder {
    val common = GrenadeCommonConfig(2.5)
    val firegrenade = FireGrenadeCommonConfig()
    override fun build(builder: ForgeConfigSpec.Builder) {
        common.build(builder)
        firegrenade.build(builder)
    }
}