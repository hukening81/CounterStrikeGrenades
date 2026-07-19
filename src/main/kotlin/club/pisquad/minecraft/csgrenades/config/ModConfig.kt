package club.pisquad.minecraft.csgrenades.config

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.config.sections.PhysicsConfig
import club.pisquad.minecraft.csgrenades.config.sections.ThrowConfig
import net.minecraftforge.common.ForgeConfigSpec

object ModConfig {
    val SPEC: ForgeConfigSpec

    val messageRange: ForgeConfigSpec.DoubleValue
    val physics = PhysicsConfig
    val throwConfig = ThrowConfig

    init {
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

        GrenadeType.entries.forEach {
            builder.push(it.resourceKey)
            it.implementation.buildConfig(builder)
            builder.pop()
        }


        SPEC = builder.build()
    }
}
