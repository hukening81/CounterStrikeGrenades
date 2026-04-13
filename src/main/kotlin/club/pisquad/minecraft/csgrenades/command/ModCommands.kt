package club.pisquad.minecraft.csgrenades.command

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeCommands
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

interface GrenadeCommandBuilder {
    fun build(dispatcher: CommandDispatcher<CommandSourceStack>)
}

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object ModCommands {
    @JvmStatic
    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        RegisterObjectivesCommand.register(event.dispatcher)

        SmokeGrenadeCommands.build(event.dispatcher)
    }
}
