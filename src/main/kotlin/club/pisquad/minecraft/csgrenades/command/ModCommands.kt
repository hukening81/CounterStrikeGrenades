package club.pisquad.minecraft.csgrenades.command

import club.pisquad.minecraft.csgrenades.*
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object ModCommands {
    @JvmStatic
    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        RegisterObjectivesCommand.register(event.dispatcher)
        SetConfigCommand.register(event.dispatcher)
    }
}
