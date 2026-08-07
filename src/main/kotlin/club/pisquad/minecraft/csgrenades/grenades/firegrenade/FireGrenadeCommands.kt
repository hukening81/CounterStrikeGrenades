package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.command.buildCommandFromBoolean
import com.mojang.brigadier.Command
import net.minecraft.commands.Commands
import net.minecraftforge.event.RegisterCommandsEvent

object FireGrenadeCommands {
    fun register(event: RegisterCommandsEvent) {
        event.dispatcher.register(
            Commands.literal(CounterStrikeGrenades.ID).then(
                Commands.literal("firegrenade").then(buildCommandFromBoolean("debugVoxel") { value ->
                    FireGrenadeOptions.debugMode = value
                    Command.SINGLE_SUCCESS
                }.requires { it.hasPermission(Commands.LEVEL_ADMINS) })
            )
        )
    }
}