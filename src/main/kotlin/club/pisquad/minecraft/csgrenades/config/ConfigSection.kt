package club.pisquad.minecraft.csgrenades.config

import net.minecraftforge.common.ForgeConfigSpec

interface ConfigSection {
    fun build(builder: ForgeConfigSpec.Builder)
}
