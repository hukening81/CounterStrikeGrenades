package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.command.buildCommandFromEnum
import com.mojang.brigadier.Command
import net.minecraft.commands.Commands
import net.minecraftforge.event.RegisterCommandsEvent


object SmokeGrenadeCommands {
    fun register(event: RegisterCommandsEvent) {
        event.dispatcher.register(
            Commands.literal(CounterStrikeGrenades.ID).then(
                Commands.literal("smoke").then(
                    buildCommandFromEnum("voxelDebug", VoxelDebugMode::class.java) {
                        SmokeGrenadeOptions.voxelDebugMode = it
                        Command.SINGLE_SUCCESS
                    }.requires { it.hasPermission(Commands.LEVEL_ADMINS) }
                )
            )
        )
    }
}