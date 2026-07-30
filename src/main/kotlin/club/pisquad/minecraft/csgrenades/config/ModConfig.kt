package club.pisquad.minecraft.csgrenades.config

import club.pisquad.minecraft.csgrenades.config.sections.PhysicsConfig
import club.pisquad.minecraft.csgrenades.config.sections.ThrowConfig
import net.minecraftforge.common.ForgeConfigSpec

interface ConfigBuilder {
    fun build(builder: ForgeConfigSpec.Builder) {}
}

object ModConfig {
    lateinit var SPEC: ForgeConfigSpec

    lateinit var messageRange: ForgeConfigSpec.DoubleValue

    var hasBuilt = false
    val physics = PhysicsConfig
    val throwConfig = ThrowConfig

    val sections: MutableList<Pair<String, ConfigBuilder>> = mutableListOf()

    fun addSection(name: String, builder: ConfigBuilder) {
        if (hasBuilt) {
            throw Exception("Add config section after the config has already built, this is not an intended behaviour")
        }
        this.sections.add(Pair(name, builder))
    }

    fun build(): ForgeConfigSpec {
        hasBuilt = true
        val builder = ForgeConfigSpec.Builder()
        builder.comment("Configuration entries for Counter Strike Grenade")
        builder.comment("")

        builder.comment(
            "Grenade related messages and events, such as explosion," +
                    " flashbang's blinding effect, decoy's sound effects, should be sent to players within this range." +
                    " It is used to bypass Minecraft's viewing distance and simulation distance's limitation " +
                    "This value should be greater then server's viewing distance and simulation distance (*16) for csgrenades " +
                    "to function properly"
        )
        messageRange = builder.defineInRange("entity_message_range", 250.0, 32.0, 500.0)

        physics.build(builder)
        throwConfig.build(builder)

        sections.sortedBy { it.first }.forEach {
            builder.push(it.first)
            it.second.build(builder)
            builder.pop()
        }

        SPEC = builder.build()
        return SPEC
    }
}