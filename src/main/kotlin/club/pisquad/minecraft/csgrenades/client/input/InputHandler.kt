package club.pisquad.minecraft.csgrenades.client.input

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.api.CSGrenadeClientAPI
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.RunnableTask
import club.pisquad.minecraft.csgrenades.core.TaskRunner
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import com.electronwill.nightconfig.core.conversion.InvalidValueException
import com.mojang.blaze3d.platform.InputConstants
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

        val currentTime = System.currentTimeMillis()
        if (InputUtils.buttonState.any()
            && InputUtils.isHoldingGrenade
            && !InputUtils.screenState
            && (token == null || TaskRunner.isDone(token!!))
            && (currentTime - lastUse).div(1000) > InputUtils.cooldown
        ) {
            val task = ThrowActionTask(
                currentTime,
                InputUtils.holdingGrenadeType!!,
                InputUtils.selectedSlot
            )
            token = TaskRunner.add(task)
        }
    }

//    fun getCurrentStrength(): Double? {
//        val task = (TaskRunner.getOrNull(token ?: return null) ?: return null) as ThrowActionTask
//        return task.strength
//    }
}

class ThrowActionTask(
    private val startTime: Long,
    private val grenade: GrenadeType,
    private val slot: Int,
) : RunnableTask<Unit> {
    private var prevStage: ThrowActionStage = ThrowActionStage.BEGIN
    private var lastJumpKeyPress: Long = 0
    private var lastButtonState: ButtonState = ButtonState.empty()
//
//    var strength: Double = 0.0
//    private val strengthTransitionSpeed: Double =
//        1.0.div(ModConfig.throwConfig.strength_transition_time.get()).div(20.0)

    override var state = Unit
    override fun runTask(s: Unit): Pair<Unit, Boolean> {
        val currentTime = System.currentTimeMillis()
        val delta = (currentTime - startTime).div(1000.0)
        val buttonState = InputUtils.buttonState

        // Abort mission, don't care about progress
        if (InputUtils.screenState || InputUtils.selectedSlot != slot) {
            return Pair(Unit, true)
        }

        if (InputUtils.jumpKeyState) {
            lastJumpKeyPress = currentTime
        }

        val currentActionStage = getCurrentActionStage(delta)


        // Stop input
        if (!buttonState.any()) {
            if (currentActionStage == ThrowActionStage.ADJUST) {
                val jumpThrow = (currentTime - lastJumpKeyPress).div(1000.0) < ModSettings.JUMP_THROW_TIME_WINDOW
                val throwType = lastButtonState.toThrowType()
                if (throwType != null) {
                    CSGrenadeClientAPI.player.throwGrenadeFromLocalPlayer(throwType, grenade, jumpThrow)
                }
            }
            return Pair(Unit, true)
        }

        lastButtonState = buttonState


        when (currentActionStage) {
            ThrowActionStage.BEGIN -> {
                throw InvalidValueException("This should not be possible")
            }

            ThrowActionStage.PINPULL_START -> {
                if (updateActionStage(currentActionStage)) {
                    CSGrenadeClientAPI.sound.playPinPullStart(grenade)
                }
            }

            ThrowActionStage.PINPULL -> {
                if (updateActionStage(currentActionStage)) {
                    CSGrenadeClientAPI.sound.playPinPull(grenade)
                }
            }

            ThrowActionStage.ADJUST -> {
                updateActionStage(currentActionStage)
//                val strengthTarget = buttonState.toStrengthTarget().strength
//                if (strengthTarget > strength) {
//                    strength += strengthTransitionSpeed
//                    if (strength > strengthTarget) {
//                        strength = strengthTarget
//                    }
//                } else if (strengthTarget < strength) {
//                    strength -= strengthTransitionSpeed
//                    if (strength < strengthTarget) {
//                        strength = strengthTarget
//                    }
//                }
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

    fun toThrowType(): ThrowType? {
        return if (primary && secondary) {
            ThrowType.MEDIUM
        } else if (primary) {
            ThrowType.STRONG
        } else if (secondary) {
            ThrowType.WEAK
        } else {
            null
        }
    }

//    fun toStrengthTarget(): StrengthTarget {
//        return when (Pair(primary, secondary)) {
//            Pair(true, true) -> {
//                StrengthTarget.MEDIUM
//            }
//
//            Pair(true, false) -> {
//                StrengthTarget.STRONG
//            }
//
//            Pair(false, true) -> {
//                StrengthTarget.WEAK
//            }
//
//            Pair(false, false) -> {
//                StrengthTarget.NONE
//            }
//
//            else -> {
//                throw Exception("This should never happen")
//            }
//        }
//    }

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

    val jumpKeyState: Boolean
        get() {
            val mapping = Minecraft.getInstance().options.keyJump
            val keycode = mapping.key.value
            return InputConstants.isKeyDown(Minecraft.getInstance().window.window, keycode)
        }
}