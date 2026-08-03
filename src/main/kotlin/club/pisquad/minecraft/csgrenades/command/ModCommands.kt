package club.pisquad.minecraft.csgrenades.command

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object ModCommands {
    private val registeredTasks: MutableSet<(RegisterCommandsEvent) -> Unit> = mutableSetOf()

    fun addRegisterTask(task: (RegisterCommandsEvent) -> Unit) {
        this.registeredTasks.add(task)
    }

    @JvmStatic
    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        RegisterObjectivesCommand.register(event.dispatcher)
        registeredTasks.forEach { it.invoke(event) }
    }
}
