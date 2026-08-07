package club.pisquad.minecraft.csgrenades.config.sections

import club.pisquad.minecraft.csgrenades.config.ConfigBuilder
import net.minecraftforge.common.ForgeConfigSpec

object PhysicsConfig : ConfigBuilder {
    lateinit var ignoreBarrierBlock: ForgeConfigSpec.BooleanValue

    override fun build(builder: ForgeConfigSpec.Builder) {
        builder.push("physics")
        ignoreBarrierBlock = builder.define("ignore_barrier_block", false)
        builder.pop()
    }
}
