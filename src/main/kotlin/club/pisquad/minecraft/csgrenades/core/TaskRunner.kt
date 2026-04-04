package club.pisquad.minecraft.csgrenades.core

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.util.*


@Mod.EventBusSubscriber(
    modid = CounterStrikeGrenades.ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = [Dist.CLIENT, Dist.DEDICATED_SERVER]
)
object TaskRunner {
    private val tasks: MutableMap<RegistrationToken, RunnableTask<out Any>> = mutableMapOf()

    typealias RegistrationToken = UUID

    fun add(task: RunnableTask<out Any>): RegistrationToken {
        val token = UUID.randomUUID()
        tasks[token] = task
        return token
    }

    fun isDone(token: RegistrationToken): Boolean {
        return tasks[token] == null
    }

    fun getOrNull(token: RegistrationToken): RunnableTask<out Any>? {
        return tasks[token]
    }

    @JvmStatic
    @SubscribeEvent
    fun onServerTick(event: TickEvent.ServerTickEvent) {
        if (event.phase != TickEvent.Phase.END) {
            return
        }
        tick()
    }

    @JvmStatic
    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) {
            return
        }
        tick()
    }

    private fun tick() {
        val scheduleRemove: MutableList<UUID> = mutableListOf()
        tasks.forEach { (uuid, task) ->
            if (task.invoke()) {
                scheduleRemove.add(uuid)
            }
        }
        scheduleRemove.forEach { tasks.remove(it) }
    }

}

interface RunnableTask<S> {
    var state: S
    fun runTask(s: S): Pair<S, Boolean>

    fun invoke(): Boolean {
        val (s, r) = runTask(state)
        state = s
        return r
    }
}

class SimpleTask<S>(override var state: S, val task: (S) -> Pair<S, Boolean>) : RunnableTask<S> {
    override fun runTask(s: S): Pair<S, Boolean> {
        return task.invoke(s)
    }
}


class OneShotTask(val task: () -> Unit) : RunnableTask<Unit> {
    override var state = Unit

    override fun runTask(s: Unit): Pair<Unit, Boolean> {
        task.invoke()
        return Pair(Unit, true)
    }
}

class RepeatedTask<S>(
    override var state: S,
    val task: (S) -> S,
    var count: Int,
) : RunnableTask<S> {

    override fun runTask(s: S): Pair<S, Boolean> {
        return if (count > 1) {
            Pair(task.invoke(s), false)
        } else {
            Pair(s, true)
        }
    }
}

class DelayedTask<S>(
    override var state: S,
    val task: (S) -> Unit,
    var delay: Int,
) : RunnableTask<S> {
    override fun runTask(s: S): Pair<S, Boolean> {
        delay--
        // This might not be accurate, but...
        return if (delay < 1) {
            task.invoke(s)
            Pair(s, true)
        } else {
            Pair(s, false)
        }
    }
}