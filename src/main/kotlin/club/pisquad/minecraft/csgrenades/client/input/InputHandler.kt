package club.pisquad.minecraft.csgrenades.client.input

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.item.CounterStrikeGrenadeItem
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
        return ModConfig.Throw.COOLDOWN.get().toTick().toInt()
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


//
//@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
//object InputHandler {
//    const val PRIMARY_BUTTON = 1
//    const val SECONDARY_BUTTON = 2
//
//    var screenState: Boolean = false
//    var oldScreenState: Boolean = false
//
//    var itemInHand: Class<out Item> = AirItem::class.java
//    var oldItemInHand: Class<out Item> = AirItem::class.java
//
//    var buttonState: ButtonState = ButtonState(primary = false, secondary = false)
//    var oldButtonState: ButtonState = ButtonState(primary = false, secondary = false)
//
//    var inputState: InputState = InputState.IDLE
//
////    var itemInHandWhenMouseDown: Class<out Item> = AirItem::class.java
////    var mouseDownWhenScreenOpen: Boolean = false
////    var inputState: InputState = InputState.IDLE
////    var buttonState: ButtonState = ButtonState(primary = false, secondary = false)
////    var waitingForRelease: Boolean = false
//
//
//    @JvmStatic
//    @SubscribeEvent
//    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
//        // Only run on client side
//        if (event.side == LogicalSide.SERVER) return
//        // Check only once a tick
//        if (event.phase == TickEvent.Phase.START) return
//
//        buttonState = getButtonState()
//        inputState = getInputState()
//
//        when (inputState) {
//            InputState.INPUTING -> {}
//            InputState.IDLE -> {}
//            InputState.ABORT -> {
//                // Cleanup is done in getInputState()
//            }
//        }
//    }
//
//    fun reset() {
//        inputState = InputState.IDLE
////        progress
//    }
//
//    fun getInputState(): InputState {
//        val screen = isScreenOpen()
//        if (buttonState.any()) {
//            if (screen) {
//                mouseDownWhenScreenOpen = true
//                return when (inputState) {
//                    InputState.INPUTING -> {
//                        InputState.ABORT
//                    }
//
//                    InputState.IDLE -> {
//                        InputState.IDLE
//                    }
//
//                    InputState.ABORT -> {
//                        throw Exception("Logical Error in Code, Please open an issue")
//                    }
//                }
//            } else {
//                if (getItemInHand() != itemInHandWhenMouseDown)
//                    return when (inputState) {
//                        InputState.INPUTING -> {
//                            InputState.ABORT
//                        }
//
//                        InputState.IDLE -> {
//                            InputState.IDLE
//                        }
//
//                        InputState.ABORT -> {
//                            throw Exception("Logical Error in Code, Please open an issue")
//                        }
//                    }
//            }
//        }
//
//
//        when (inputState) {
//            InputState.INPUTING -> {
//
//            }
//
//            InputState.IDLE -> {
//
//            }
//
//            InputState.ABORT -> {
//
//            }
//        }
//        TODO()
//    }
//
//    fun getPlayer(): LocalPlayer {
//        return Minecraft.getInstance().player!!
//    }
//
//    fun getItemInHand(): Class<out Item> {
//        return getPlayer().mainHandItem.item.javaClass
//    }
//
//    fun getButtonState(): ButtonState {
//        val handle = Minecraft.getInstance().window.window
//        val primary = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS
//        val secondary = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS
//        return ButtonState(primary, secondary)
//    }
//
//    fun isScreenOpen(): Boolean {
//        return Minecraft.getInstance().screen != null
//    }
//
//    class ButtonState(val primary: Boolean, val secondary: Boolean) {
//        fun any(): Boolean {
//            return primary || secondary
//        }
//    }
//
//    enum class InputState {
//        INPUTING,
//        IDLE,
//        ABORT,
//    }
//}


//
//import club.pisquad.minecraft.csgrenades.*
//import club.pisquad.minecraft.csgrenades.config.ModConfig
//import club.pisquad.minecraft.csgrenades.enums.GrenadeType
//import club.pisquad.minecraft.csgrenades.event.GrenadeThrowEvent
//import club.pisquad.minecraft.csgrenades.item.*
//import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
//import club.pisquad.minecraft.csgrenades.network.message.GrenadeThrownMessage
//import net.minecraft.client.Minecraft
//import net.minecraft.core.Rotations
//import net.minecraft.world.InteractionHand
//import net.minecraft.world.entity.player.Player
//import net.minecraftforge.api.distmarker.Dist
//import net.minecraftforge.api.distmarker.OnlyIn
//import net.minecraftforge.common.MinecraftForge
//import net.minecraftforge.event.TickEvent
//import net.minecraftforge.event.TickEvent.ClientTickEvent
//import net.minecraftforge.eventbus.api.SubscribeEvent
//import net.minecraftforge.fml.common.Mod
//import org.lwjgl.glfw.GLFW
//import java.time.Duration
//import java.time.Instant
//import kotlin.math.min
//
////
////@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
////object InputHandler {
////    var readyForInput: Boolean = false
////    var lastGrenadeInHand: GrenadeType? = null
////
////    @JvmStatic
////    @SubscribeEvent
////    fun onClientTick(event: ClientTickEvent) {
////        if (event.phase == TickEvent.Phase.START) return
////        val player = Minecraft.getInstance().player ?: return
////        if (Minecraft.getInstance().screen != null || player.getItemInHand(InteractionHand.MAIN_HAND).item !is CounterStrikeGrenadeItem) {
////            readyForInput = false
////            return
////        }
////
////        val (primary, secondary) = getButtonState()
////        if (!readyForInput) {
////            if (primary || secondary) {
////                return
////            } else {
////                readyForInput = true
////                return // Wait for next tick
////            }
////        }
////    }
////
////    private fun clearState() {
////
////    }
////
////    private fun getButtonState(): Pair<Boolean, Boolean> {
////        val window = Minecraft.getInstance().window.window
////        return Pair(
////            GLFW.glfwGetMouseButton(
////                window,
////                GLFW.GLFW_MOUSE_BUTTON_LEFT,
////            ) == 1,
////            GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == 1,
////        )
////    }
////
////    object InputState {
////        var speedFactor
////    }
////
////}
//
//@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
//object InputHandler {
//    private var grenadeLastThrow: Instant = Instant.now()
//    private var primaryButtonPressed: Boolean = false
//    private var secondaryButtonPressed: Boolean = false
//    private var throwSpeedTransientTarget: Double? = null
//    private var throwSpeedTransientOrigin: Double? = null
//    var currentThrowSpeed: Double? = null
//    var chargeStartTime: Instant? = null
//    private var transientBeginTime: Instant = Instant.now()
//    private var previousSlot = -1
//    private var screenOpened = false
//    private var buttonPressedWhenScreenOpen = false
//
//    @JvmStatic
//    @SubscribeEvent
//    fun onClientTick(event: ClientTickEvent) {
//        if (event.phase == TickEvent.Phase.START) return
//
//        val (primaryButtonPressed, secondaryButtonPressed) = getButtonState()
//
//        // Test if any screen is opened (e.g. inventory, chat, menu, etc.)
//        if (Minecraft.getInstance().screen != null) {
//            this.screenOpened = true
//            if (this.primaryButtonPressed || this.secondaryButtonPressed) {
//                this.cleanProgress()
//                this.buttonPressedWhenScreenOpen = true
//            }
//            return
//        } else if (screenOpened) {
//            this.buttonPressedWhenScreenOpen = primaryButtonPressed || secondaryButtonPressed
//            this.screenOpened = false
//            return
//        }
//        if (buttonPressedWhenScreenOpen) {
//            if (!(primaryButtonPressed || secondaryButtonPressed)) {
//                this.buttonPressedWhenScreenOpen = false
//                return
//            }
//            return
//        }
//
//        val player = Minecraft.getInstance().player ?: return
//        val selectedSlot = player.inventory.selected
//
//        val itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND)
//
//        if (itemInHand.item is CounterStrikeGrenadeItem &&
//            previousSlot == selectedSlot
//        ) {
//            if (Duration.between(this.grenadeLastThrow, Instant.now())
//                    .toMillis() > ModConfig.GRENADE_THROW_COOLDOWN.get()
//            ) {
//                when (Pair(this.primaryButtonPressed, this.secondaryButtonPressed)) {
//                    Pair(false, false) -> {
//                        if (primaryButtonPressed || secondaryButtonPressed) {
//                            this.chargeStartTime = Instant.now()
//                        }
//                        when (Pair(primaryButtonPressed, secondaryButtonPressed)) {
//                            Pair(true, true) -> {
//                                this.setNewTransientTarget(
//                                    ModConfig.THROW_SPEED_MODERATE.get(),
//                                    ModConfig.THROW_SPEED_MODERATE.get(),
//                                )
//                            }
//
//                            Pair(true, false) -> {
//                                this.setNewTransientTarget(
//                                    ModConfig.THROW_SPEED_STRONG.get(),
//                                    ModConfig.THROW_SPEED_STRONG.get(),
//                                )
//                            }
//
//                            Pair(false, true) -> {
//                                this.setNewTransientTarget(
//                                    ModConfig.THROW_SPEED_WEAK.get(),
//                                    ModConfig.THROW_SPEED_WEAK.get(),
//                                )
//                            }
//                        }
//                    }
//
//                    Pair(true, false) -> {
//                        when (Pair(primaryButtonPressed, secondaryButtonPressed)) {
//                            Pair(true, true) -> {
//                                this.setNewTransientTarget(ModConfig.THROW_SPEED_MODERATE.get(), null)
//                            }
//
//                            Pair(false, true) -> {
//                                this.setNewTransientTarget(ModConfig.THROW_SPEED_WEAK.get(), null)
//                            }
//                        }
//                    }
//
//                    Pair(false, true) -> {
//                        when (Pair(primaryButtonPressed, secondaryButtonPressed)) {
//                            Pair(true, true) -> {
//                                this.setNewTransientTarget(ModConfig.THROW_SPEED_MODERATE.get(), null)
//                            }
//
//                            Pair(true, false) -> {
//                                this.setNewTransientTarget(ModConfig.THROW_SPEED_STRONG.get(), null)
//                            }
//                        }
//                    }
//
//                    Pair(true, true) -> {
//                        when (Pair(primaryButtonPressed, secondaryButtonPressed)) {
//                            Pair(true, false) -> {
//                                this.setNewTransientTarget(ModConfig.THROW_SPEED_STRONG.get(), null)
//                            }
//
//                            Pair(false, true) -> {
//                                this.setNewTransientTarget(ModConfig.THROW_SPEED_WEAK.get(), null)
//                            }
//                        }
//                    }
//                }
//                this.updateCurrentSpeed()
//                if (!primaryButtonPressed && !secondaryButtonPressed) {
//                    if (this.primaryButtonPressed || this.secondaryButtonPressed) {
//                        val grenadeType = (itemInHand.item as CounterStrikeGrenadeItem).grenadeType
//                        throwAction(this.currentThrowSpeed ?: 0.0, grenadeType)
//                        setItemCoolDown(player)
//                        cleanProgress()
//                        this.grenadeLastThrow = Instant.now()
//                    }
//                }
//                this.primaryButtonPressed = primaryButtonPressed
//                this.secondaryButtonPressed = secondaryButtonPressed
//            }
//        } else {
//            this.primaryButtonPressed = false
//            this.secondaryButtonPressed = false
//            this.cleanProgress()
//        }
//        this.previousSlot = selectedSlot
//    }
//
//    private fun cleanProgress() {
//        this.primaryButtonPressed = false
//        this.secondaryButtonPressed = false
//        this.currentThrowSpeed = null
//        this.throwSpeedTransientTarget = null
//        this.throwSpeedTransientOrigin = null
//        this.chargeStartTime = null
//    }
//
//    private fun setNewTransientTarget(speedTarget: Double, speedOrigin: Double?) {
//        this.throwSpeedTransientOrigin = speedOrigin ?: this.currentThrowSpeed
//        this.throwSpeedTransientTarget = speedTarget
//        this.transientBeginTime = Instant.now()
//    }
//
//    private fun updateCurrentSpeed() {
//        if (throwSpeedTransientOrigin == null || throwSpeedTransientTarget == null) return
//        this.currentThrowSpeed = linearInterpolate(
//            this.throwSpeedTransientOrigin!!,
//            this.throwSpeedTransientTarget!!,
//            min(
//                1.0,
//                Duration.between(this.transientBeginTime, Instant.now()).toMillis()
//                    .toDouble() / ModConfig.THROW_TYPE_TRANSIENT_TIME.get(),
//            ),
//        )
//    }
//
//    private fun getButtonState(): Pair<Boolean, Boolean> {
//        val window = Minecraft.getInstance().window.window
//        return Pair(
//            GLFW.glfwGetMouseButton(
//                window,
//                GLFW.GLFW_MOUSE_BUTTON_LEFT,
//            ) == 1,
//            GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == 1,
//        )
//    }
//}
//
//fun throwAction(throwSpeed: Double, grenadeType: GrenadeType) {
//    val player: Player = Minecraft.getInstance().player ?: return
//    val itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND) // Moved up for event
//
//    // NEW: Fire the GrenadeThrowEvent
//    val event = GrenadeThrowEvent(player, itemInHand, grenadeType)
//    if (MinecraftForge.EVENT_BUS.post(event)) {
//        // Event was canceled, stop the throw action
//        return
//    }
//
//    val speedFactor =
//        (throwSpeed - ModConfig.THROW_SPEED_WEAK.get()) / (ModConfig.THROW_SPEED_STRONG.get() - ModConfig.THROW_SPEED_WEAK.get())
//    val playerSpeedFactor =
//        linearInterpolate(
//            ModConfig.PLAYER_SPEED_FACTOR_WEAK.get(),
//            ModConfig.PLAYER_SPEED_FACTOR_STRONG.get(),
//            speedFactor,
//        )
//
//    val speed = player.deltaMovement.scale(playerSpeedFactor)
//        .add(
//            player.lookAngle.normalize()
//                .scale(
//                    linearInterpolate(
//                        ModConfig.THROW_SPEED_WEAK.get(),
//                        ModConfig.THROW_SPEED_STRONG.get(),
//                        speedFactor,
//                    ),
//                ),
//        )
//        .length()
//
//    // --- NBT Reading Logic for Decoy ---
//    var customSound: String? = null
//    if (grenadeType == GrenadeType.DECOY) {
//        val itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND)
//        val tag = itemInHand.tag
//        // 8 is the NBT tag ID for String
//        if (tag != null && tag.contains("DecoySound", 8)) {
//            customSound = tag.getString("DecoySound")
//        }
//    }
//    // --- End NBT Reading ---
//
//    ModPacketHandler.INSTANCE.sendToServer(
//        GrenadeThrownMessage(
//            player.uuid,
//            speed,
//            grenadeType,
//            player.eyePosition,
//            Rotations(player.xRot, player.yRot, 0.0f),
////            customSound,
//        ),
//    )
//}
//
//fun setItemCoolDown(player: Player) {
//    player.inventory.items.forEach {
//        if (it.item is CounterStrikeGrenadeItem) {
//            player.cooldowns.addCooldown(it.item, ModConfig.GRENADE_THROW_COOLDOWN.get() / 1000 * 20)
//        }
//    }
//}
