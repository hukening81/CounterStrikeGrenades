package club.pisquad.minecraft.csgrenades.config.sections

import club.pisquad.minecraft.csgrenades.config.ConfigSection
import net.minecraftforge.common.ForgeConfigSpec

object PhysicsConfig : ConfigSection {
    lateinit var ignoreBarrierBlock: ForgeConfigSpec.BooleanValue

    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.push("physics")
        ignoreBarrierBlock = builder.define("ignore_barrier_block", false)
        builder.pop()
    }
}
