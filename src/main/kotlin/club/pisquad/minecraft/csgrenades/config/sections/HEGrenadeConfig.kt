package club.pisquad.minecraft.csgrenades.config.sections

import club.pisquad.minecraft.csgrenades.config.ConfigSection
import club.pisquad.minecraft.csgrenades.config.common.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.GrenadeType
import net.minecraftforge.common.ForgeConfigSpec

object HEGrenadeConfig : ConfigSection {
    val grenadeCommonConfig = GrenadeCommonConfig(2.5)
    lateinit var explosionRadius: ForgeConfigSpec.DoubleValue
    lateinit var damageBoostNearHead: ForgeConfigSpec.DoubleValue
    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.push(GrenadeType.HE_GRENADE.resourceKey)
        grenadeCommonConfig.build(builder)
        explosionRadius = builder.defineInRange("explosion_radius", 2.5, 1.0, 10.0)
        damageBoostNearHead = builder.defineInRange("damage_boost_near_head", 1.5, 0.1, 10.0)
        builder.pop()
    }
}
