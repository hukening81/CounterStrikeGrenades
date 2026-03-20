package club.pisquad.minecraft.csgrenades.config.sections

import club.pisquad.minecraft.csgrenades.SERVER_MESSAGE_RANGE
import club.pisquad.minecraft.csgrenades.config.ConfigSection
import club.pisquad.minecraft.csgrenades.config.common.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import net.minecraftforge.common.ForgeConfigSpec

object FlashBangConfig : ConfigSection {
    val grenadeCommonConfig = GrenadeCommonConfig(1.4)
    lateinit var blindEffectRanges: ForgeConfigSpec.ConfigValue<List<Double>>
    lateinit var blindEffectFadingRange: ForgeConfigSpec.DoubleValue
    lateinit var blindEffectMaximumRadius: ForgeConfigSpec.DoubleValue

    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.push(GrenadeType.FLASH_BANG.resourceKey)
        grenadeCommonConfig.build(builder)
        builder.comment("Defined as a list of triples in following format: [Angle from player's aim(degrees), Time of full blindness, Total time of blinding effects]")
        builder.comment("Angles from two adjacent triples define a range")
        builder.comment("See cs:go's data: https://counterstrike.fandom.com/wiki/Flashbang")
        blindEffectRanges = builder.defineList(
            "bind_effect_ranges", listOf(
                53.0, 1.88, 4.87,
                72.0, 0.45, 3.4,
                101.0, 0.08, 1.95,
                180.0, 0.08, 0.95,
            )
        ) { it is Double && it < 181 }
        blindEffectFadingRange =
            builder.defineInRange("blind_effect_fading_range", 50.0, 1.0, SERVER_MESSAGE_RANGE.toDouble())

        blindEffectMaximumRadius =
            builder.defineInRange("blind_effect_decay_ratio", 50.0, 10.0, SERVER_MESSAGE_RANGE.toDouble())

        builder.pop()
    }
}
