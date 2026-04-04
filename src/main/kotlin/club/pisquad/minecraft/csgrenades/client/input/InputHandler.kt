package club.pisquad.minecraft.csgrenades.client.input

import club.pisquad.minecraft.csgrenades.AnimationTiming
import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.api.CSGrenadeClientAPI
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.RunnableTask
import club.pisquad.minecraft.csgrenades.core.TaskRunner
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import com.electronwill.nightconfig.core.conversion.InvalidValueException
import net.minecraft.client.Minecraft
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.Mod
import org.lwjgl.glfw.GLFW

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object InputHandler {

    var token: TaskRunner.RegistrationToken? = null
    var lastUse: Long = 0

    @JvmStatic
    @SubscribeEvent
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        if (event.side == LogicalSide.SERVER) return
        if (event.phase == TickEvent.Phase.END) return

        val strengthTarget = InputUtils.buttonState.toStrengthTarget()
        val currentTime = System.currentTimeMillis()
        if (strengthTarget != StrengthTarget.NONE
            && InputUtils.isHoldingGrenade
            && !InputUtils.screenState
            && (token == null || TaskRunner.isDone(token!!))
            && (currentTime - lastUse).div(1000) > InputUtils.cooldown
        ) {
            val task = ThrowActionTask(
                currentTime,
                InputUtils.holdingGrenadeType!!,
                strengthTarget,
                InputUtils.selectedSlot
            )
            token = TaskRunner.add(task)
        }
    }
//    fun setInventoryCoolDown() {
//        val player = Minecraft.getInstance().player!!
//        player.inventory.items.forEach {
//            if (it.item is CounterStrikeGrenadeItem) {
//                player.cooldowns.addCooldown(it.item, InputUtils.cooldown.toTick().toInt())
//            }
//        }
//    }
}

class ThrowActionTask(
    private val startTime: Long,
    private val grenadeType: GrenadeType,
    private var strengthTarget: StrengthTarget,
    private val slot: Int,
) : RunnableTask<Unit> {
    private var prevStage: ThrowActionStage = ThrowActionStage.BEGIN

    override var state = Unit
    override fun runTask(s: Unit): Pair<Unit, Boolean> {
        val delta = (System.currentTimeMillis() - startTime).div(1000.0)

        // Abort mission!
        if (!InputUtils.buttonState.any() || InputUtils.screenState || InputUtils.selectedSlot != slot) {
            return Pair(Unit, true)
        }

        when (val currentActionStage = getCurrentActionStage(delta)) {
            ThrowActionStage.BEGIN -> {
                throw InvalidValueException("This should not be possible")
            }

            ThrowActionStage.PINPULL_START -> {
                if (updateActionStage(currentActionStage)) {
                    CSGrenadeClientAPI.sound.playPinPullStart(grenadeType)
                }
            }

            ThrowActionStage.PINPULL -> {
                if (updateActionStage(currentActionStage)) {
                    CSGrenadeClientAPI.sound.playPinPull(grenadeType)
                }
            }

            ThrowActionStage.ADJUST -> {

            }
        }
        return Pair(Unit, false)
    }

    private fun updateActionStage(current: ThrowActionStage): Boolean {
        if (current == prevStage) {
            return false
        } else {
            prevStage = current
            return true
        }
    }

    companion object {
        private fun getCurrentActionStage(delta: Double): ThrowActionStage {
            return if (delta < AnimationTiming.PIN_PULL_START) {
                ThrowActionStage.PINPULL_START
            } else if (delta < AnimationTiming.PIN_PULL_START + AnimationTiming.PIN_PULL) {
                ThrowActionStage.PINPULL
            } else {
                ThrowActionStage.ADJUST
            }
        }
    }

    private enum class ThrowActionStage {
        BEGIN,
        PINPULL_START,
        PINPULL,
        ADJUST,
    }

}

data class ButtonState(val primary: Boolean, val secondary: Boolean) {
    fun any(): Boolean {
        return primary || secondary
    }

    fun toStrengthTarget(): StrengthTarget {
        return when (Pair(primary, secondary)) {
            Pair(true, true) -> {
                StrengthTarget.MEDIUM
            }

            Pair(true, false) -> {
                StrengthTarget.STRONG
            }

            Pair(false, true) -> {
                StrengthTarget.WEAK
            }

            Pair(false, false) -> {
                StrengthTarget.NONE
            }

            else -> {
                throw Exception("This should never happen")
            }
        }
    }

    companion object {
        fun empty(): ButtonState {
            return ButtonState(false, secondary = false)
        }

        fun current(): ButtonState {
            val handle = Minecraft.getInstance().window.window
            val primary = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS
            val secondary = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS
            return ButtonState(primary, secondary)
        }
    }
}

private object InputUtils {
    val buttonState: ButtonState
        get() {
            return ButtonState.current()
        }

    val screenState: Boolean
        get() {
            return Minecraft.getInstance().screen != null
        }
    val selectedSlot: Int
        get() {
            return Minecraft.getInstance().player!!.inventory.selected
        }

    val isHoldingGrenade: Boolean
        get() {
            return holdingGrenadeType != null
        }

    val holdingGrenadeType: GrenadeType?
        get() {
            val item = Minecraft.getInstance().player!!.mainHandItem.item
            return if (item is CounterStrikeGrenadeItem) {
                item.grenadeType
            } else {
                null
            }
        }

    val cooldown: Double
        get() {
            return ModConfig.throwConfig.cooldown.get()
        }
}

enum class StrengthTarget(val strength: Double) {
    NONE(0.0), WEAK(1.0), MEDIUM(2.0), STRONG(3.0),
}