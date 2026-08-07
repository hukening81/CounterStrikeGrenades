package club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov

import club.pisquad.minecraft.csgrenades.config.ConfigBuilder
import club.pisquad.minecraft.csgrenades.config.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeConfig
import net.minecraftforge.common.ForgeConfigSpec

object MolotovConfig : ConfigBuilder, FireGrenadeConfig {
    override val common = GrenadeCommonConfig(1.5)
    override val firegrenade = FireGrenadeCommonConfig()
    override fun build(builder: ForgeConfigSpec.Builder) {
        common.build(builder)
        firegrenade.build(builder)
    }
}
