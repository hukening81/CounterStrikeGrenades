package club.pisquad.minecraft.csgrenades.client.input

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.network.message.ClientGrenadeThrowMessage
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.math.max
import kotlin.math.min

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object InputState {

    private var tickSinceStageChange: Int = 0
    var submitted: Boolean = false

    var stage: Stage = Stage.IDLE
        set(value) {
            field = value
            tickSinceStageChange = 0
        }

    val idle: Boolean
        get() {
            return stage == Stage.IDLE
        }

    var strength: Double = StrengthTarget.NONE.strength
    var lastStrength: Double = StrengthTarget.NONE.strength

    var strengthTransitionStart: Double = 0.0
    var strengthTransitionTarget: Double = 0.0


    enum class StrengthTarget(val strength: Double) {
        NONE(0.0), WEAK(1.0), MEDIUM(2.0), STRONG(3.0),
    }


    fun start(strengthTarget: StrengthTarget) {
        if (!idle) {
            throw Exception("Input already started")
        }
        stage = Stage.PULL_PIN
        strengthTransitionTarget = strengthTarget.strength
    }

    fun submit() {
        submitted = true
        when (stage) {
            Stage.PULL_PIN -> {
                // EMPTY
            }

            Stage.ACTIVE -> {
                doThrow()
                resetState()
            }

            Stage.IDLE -> {
                throw Exception("InputState submitted when actually in IDLE")
            }
        }
    }

    fun abort() {
        resetState()
    }

    private fun resetState() {
        stage = Stage.IDLE
        strength = StrengthTarget.NONE.strength
        strengthTransitionStart = 0.0
        strengthTransitionTarget = 0.0
        submitted = false
    }

    fun setTarget(target: StrengthTarget) {
        strengthTransitionStart = strength
        strengthTransitionTarget = target.strength
    }

    fun resetIfNotIdle() {
        if (!idle) {
            abort()
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) return
        if (idle) return

        when (stage) {
            Stage.IDLE -> {
                // EMPTY
            }

            Stage.PULL_PIN -> {
                if (tickSinceStageChange > Stage.PULL_PIN.tick) {
                    if (!submitted && InputHandler.buttonState.any()) {
                        stage = Stage.ACTIVE
                    } else {
                        strength = strengthTransitionTarget
                        doThrow()
                        resetState()
                    }
                }
            }

            Stage.ACTIVE -> {
                if (InputHandler.buttonState.any()) {
                    lastStrength = strength
                    strength = calculateStrength()
                } else {
                    doThrow()
                    resetState()
                }
            }
        }

        tickSinceStageChange++

    }

    fun doThrow() {
        val message = ClientGrenadeThrowMessage.fromInputState()
        if (message == null) {
            ModLogger.warn("Failed to create ClientGrenadeThrowMessage")
        } else {
            ModPacketHandler.INSTANCE.sendToServer(message)
        }
    }

    fun calculateStrength(): Double {
        return linearInterpolate(
            strengthTransitionStart,
            strengthTransitionTarget,
            min(
                1.0,
                max(
                    0.0,
                    tickSinceStageChange.toDouble().div(
                        ModConfig.throwConfig.strength_transition_time.get().toTick(),
                    ),
                ),
            ),
        )
    }

    fun calculateGrenadeSpeed(): Vec3? {
        val player = Minecraft.getInstance().player ?: return null
        val direction = player.lookAngle.normalize()
        if (strength <= 0) return null
        var speed = 0.0
        if (strength <= StrengthTarget.WEAK.strength) {
            speed = linearInterpolate(
                0.0,
                ModConfig.throwConfig.speed_weak.get(),
                (strength - StrengthTarget.NONE.strength).div(StrengthTarget.WEAK.strength - StrengthTarget.NONE.strength),
            )
        } else if (strength <= StrengthTarget.MEDIUM.strength) {
            speed = linearInterpolate(
                0.0,
                ModConfig.throwConfig.speed_medium.get(),
                (strength - StrengthTarget.WEAK.strength).div(StrengthTarget.MEDIUM.strength - StrengthTarget.WEAK.strength),
            )
        } else {
            speed = linearInterpolate(
                0.0,
                ModConfig.throwConfig.speed_strong.get(),
                (strength - StrengthTarget.MEDIUM.strength).div(StrengthTarget.STRONG.strength - StrengthTarget.MEDIUM.strength),
            )
        }
        return direction.scale(speed.toMetersPerTick())
    }

    enum class Stage(val tick: Int) {
        IDLE(-1),
        PULL_PIN(5),
        ACTIVE(-1),
    }
}
