package club.pisquad.minecraft.csgrenades.compatibility

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.registery.ModDamageType
import net.minecraft.world.entity.player.Player
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object UpdateObjectives {
    @SubscribeEvent
    fun onPlayerDied(event: LivingDeathEvent) {
        if (event.entity is Player) {
            val server = event.entity.server ?: return
            val attackName = event.source.entity?.name?.string ?: return
            val commandSource = server.createCommandSourceStack()
            if (event.source.`is`(ModDamageType.HEGRENADE_EXPLOSION) && server.scoreboard.getObjective(
                    OBJECTIVE_KILLCOUNT_HEGRENADE
                ) != null
            ) {

                server.commands.dispatcher.execute(
                    COMMAND_INCREASE_OBJECTIVE_KILLCOUNT_HEGRENADE.format(attackName),
                    commandSource
                )
            } else if (event.source.`is`(ModDamageType.INCENDIARY_FIRE) && server.scoreboard.getObjective(
                    OBJECTIVE_KILLCOUNT_INCENDIARY
                ) != null
            ) {
                server.commands.dispatcher.execute(
                    COMMAND_INCREASE_OBJECTIVE_KILLCOUNT_INCENDIARY.format(attackName), commandSource
                )
            } else if (event.source.`is`(ModDamageType.MOLOTOV_FIRE) && server.scoreboard.getObjective(
                    OBJECTIVE_KILLCOUNT_MOLOTOV
                ) != null
            ) {
                server.commands.dispatcher.execute(
                    COMMAND_INCREASE_OBJECTIVE_KILLCOUNT_MOLOTOV.format(attackName), commandSource
                )
            }
        }
    }
}