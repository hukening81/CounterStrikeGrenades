package club.pisquad.minecraft.csgrenades.config.sections

import club.pisquad.minecraft.csgrenades.config.ConfigSection
import club.pisquad.minecraft.csgrenades.config.common.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import net.minecraftforge.common.ForgeConfigSpec

object FlashBangConfig : ConfigSection {
    val grenadeCommonConfig = GrenadeCommonConfig(2.5)
    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.push(GrenadeType.FLASH_BANG.resourceKey)
        builder.pop()
    }
}
