package club.pisquad.minecraft.csgrenades.config.sections

import club.pisquad.minecraft.csgrenades.config.ConfigSection
import club.pisquad.minecraft.csgrenades.config.common.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import net.minecraftforge.common.ForgeConfigSpec

object FlashBangConfig : ConfigSection {
    val grenadeCommonConfig = GrenadeCommonConfig(2.5)

    lateinit var effectDecayFactor: ForgeConfigSpec.DoubleValue

    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.push(GrenadeType.FLASH_BANG.resourceKey)
        grenadeCommonConfig.build(builder)
        effectDecayFactor = builder.defineInRange("effect_decay_factor", 1.0, 1.0, 10.0)
        builder.pop()
    }
}
