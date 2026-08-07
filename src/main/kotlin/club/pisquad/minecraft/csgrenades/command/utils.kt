package club.pisquad.minecraft.csgrenades.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands


fun <T : Enum<T>> buildCommandFromEnum(
    sectionName: String,
    enumClass: Class<T>,
    cb: (T) -> Int
): LiteralArgumentBuilder<CommandSourceStack> {
    var baseCommand = Commands.literal(sectionName)
    enumClass.enumConstants.forEach { variant ->
        baseCommand = baseCommand.then(
            Commands.literal(variant.name.lowercase()).executes {
                cb(variant)
            }
        )
    }
    return baseCommand
}

fun buildCommandFromBoolean(sectionName: String, cb: (Boolean) -> Int): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal(sectionName).then(Commands.argument("value", BoolArgumentType.bool()).executes { context ->
        cb.invoke(BoolArgumentType.getBool(context, "value"))
    })
}