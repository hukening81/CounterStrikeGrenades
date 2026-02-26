package club.pisquad.minecraft.csgrenades.config.common

import club.pisquad.minecraft.csgrenades.config.ConfigSection
import net.minecraftforge.common.ForgeConfigSpec

@Suppress("propertyName")
class GrenadeCommonConfig(
    val defaultFuseTime: Double = 2.5,
) : ConfigSection {
    lateinit var DAMAGE_NON_PLAYER: ForgeConfigSpec.BooleanValue
    lateinit var FUSE_TIME: ForgeConfigSpec.DoubleValue

    override fun build(
        builder: ForgeConfigSpec.Builder,
    ) {
        DAMAGE_NON_PLAYER = builder.define("damage_non_player", true)
        FUSE_TIME = builder.defineInRange("fuse_time", defaultFuseTime, 0.1, 60.0)
    }
}
