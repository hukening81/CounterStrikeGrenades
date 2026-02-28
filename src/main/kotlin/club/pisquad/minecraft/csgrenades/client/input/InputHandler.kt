package club.pisquad.minecraft.csgrenades.client.input

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.item.core.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.client.Minecraft
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.Mod
import org.lwjgl.glfw.GLFW

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object InputHandler {
    const val PRIMARY_BUTTON = 1
    const val SECONDARY_BUTTON = 2

    private var readyForInput: Boolean = false

    val buttonState: ButtonState
        get() {
            val handle = Minecraft.getInstance().window.window
            val primary = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS
            val secondary = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS
            return ButtonState(primary, secondary)
        }
    val screenState: Boolean
        get() {
            return Minecraft.getInstance().screen != null
        }
    val selectedSlot: Int
        get() {
            return Minecraft.getInstance().player!!.inventory.selected
        }

    var lastButtonState: ButtonState = ButtonState.empty()
    var lastScreenState: Boolean = false
    private var lastSelectedSlot: Int = -1

    private var cooldownTimer: Int = 0


    data class ButtonState(val primary: Boolean, val secondary: Boolean) {
        fun any(): Boolean {
            return primary || secondary
        }

        fun toStrengthTarget(): InputState.StrengthTarget {
            return when (Pair(primary, secondary)) {
                Pair(true, true) -> {
                    InputState.StrengthTarget.MEDIUM
                }

                Pair(true, false) -> {
                    InputState.StrengthTarget.STRONG
                }

                Pair(false, true) -> {
                    InputState.StrengthTarget.WEAK
                }

                Pair(false, false) -> {
                    InputState.StrengthTarget.NONE
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
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        if (event.side == LogicalSide.SERVER) return
        if (event.phase == TickEvent.Phase.START) return



        if (!lastScreenState && screenState) {
            onScreenOpen()
        } else if (lastScreenState && !screenState) {
            onScreenClose()
        }

        if (!lastButtonState.primary && buttonState.primary) {
            onButtonPress(PRIMARY_BUTTON)
        } else if (lastButtonState.primary && !buttonState.primary) {
            onButtonRelease(PRIMARY_BUTTON)
        }

        if (!lastButtonState.secondary && buttonState.secondary) {
            onButtonPress(SECONDARY_BUTTON)
        } else if (lastButtonState.secondary && !buttonState.secondary) {
            onButtonRelease(SECONDARY_BUTTON)
        }

        if (selectedSlot != lastSelectedSlot) {
            onSlotChange()
        }
//        if (readyForInput && buttonState.any()) {
//            InputState.tick()
//        }

//        lastButtonState = buttonState
//        lastScreenState = screenState
//        lastSelectedSlot
        updateInternalState()
    }

    fun updateInternalState() {
        lastScreenState = screenState
        lastSelectedSlot = selectedSlot
        lastButtonState = buttonState

        if (cooldownTimer > 0) {
            cooldownTimer--
            readyForInput = false
        } else {
            readyForInput = true
        }

        if (screenState) {
            readyForInput = false
        }

        if (!isItemInHandGrenadeItem()) {
            readyForInput = false
        }
    }

    fun isItemInHandGrenadeItem(): Boolean {
        return Minecraft.getInstance().player!!.mainHandItem.item is CounterStrikeGrenadeItem
    }

    fun onScreenOpen() {
        InputState.resetIfNotIdle()
    }

    fun onScreenClose() {
        if (buttonState.any()) {
            readyForInput = false
        }
    }

    fun onButtonPress(button: Int) {
        if (screenState) return
        if (readyForInput) {
            if (InputState.idle) {
                InputState.start(buttonState.toStrengthTarget())
            } else {
                InputState.setTarget(buttonState.toStrengthTarget())
            }
        }
    }

    fun onButtonRelease(button: Int) {
        if (screenState) return

        if (!buttonState.any()) {
            if (InputState.idle) {
                if (isItemInHandGrenadeItem()) {
                    readyForInput = true
                }
            } else {
                InputState.submit()
                setInventoryCoolDown()
                cooldownTimer = getCoolDownTick()
            }
        } else {
            InputState.setTarget(buttonState.toStrengthTarget())
        }
    }

    fun onSlotChange() {
        InputState.resetIfNotIdle()
        if (buttonState.any()) {
            readyForInput = false
        }
    }

    fun getCoolDownTick(): Int {
        return ModConfig.throwConfig.cooldown.get().toTick().toInt()
    }

    fun setInventoryCoolDown() {
        val player = Minecraft.getInstance().player!!
        player.inventory.items.forEach {
            if (it.item is CounterStrikeGrenadeItem) {
                player.cooldowns.addCooldown(it.item, getCoolDownTick())
            }
        }
    }
}
