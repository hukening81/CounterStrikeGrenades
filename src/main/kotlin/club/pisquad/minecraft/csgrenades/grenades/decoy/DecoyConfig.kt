package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.config.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.config.GrenadeConfigBuilder
import net.minecraftforge.common.ForgeConfigSpec

object DecoyConfig : GrenadeConfigBuilder {
    val common = GrenadeCommonConfig()
    lateinit var soundDuration: ForgeConfigSpec.DoubleValue
    lateinit var soundMinGroupInterval: ForgeConfigSpec.DoubleValue
    lateinit var soundMaxGroupInterval: ForgeConfigSpec.DoubleValue

    override fun build(builder: ForgeConfigSpec.Builder) {
        common.build(builder)
        soundDuration = builder.defineInRange("sound_duration", 5.0, 0.0, 60.0)
        soundMinGroupInterval = builder.defineInRange("sound_min_group_interval", 1.0, 0.0, 60.0)
        soundMaxGroupInterval = builder.defineInRange("sound_max_group_interval", 1.0, 0.0, 60.0)
    }
}