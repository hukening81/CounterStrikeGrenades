package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraftforge.event.RegisterCommandsEvent


object SmokeGrenadeCommands {
    fun register(event: RegisterCommandsEvent) {
        event.dispatcher.register(
            Commands.literal(CounterStrikeGrenades.ID).then(
                Commands.literal("smoke").then(
                    buildCommandFromEnum("voxelDebug", VoxelDebugMode::class.java) {
                        SmokeGrenadeOptions.voxelDebugMode = it
                    }.requires { it.hasPermission(3) }
                )
            )
        )
    }
}

fun <T : Enum<T>> buildCommandFromEnum(
    sectionName: String,
    enumClass: Class<T>,
    cb: (T) -> Unit
): LiteralArgumentBuilder<CommandSourceStack> {
    var baseCommand = Commands.literal(sectionName)
    enumClass.enumConstants.forEach { variant ->
        baseCommand = baseCommand.then(
            Commands.literal(variant.name.lowercase()).executes {
                cb(variant)
                Command.SINGLE_SUCCESS
            }
        )
    }
    return baseCommand
}