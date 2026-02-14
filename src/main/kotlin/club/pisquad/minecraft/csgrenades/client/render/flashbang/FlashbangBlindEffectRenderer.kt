package club.pisquad.minecraft.csgrenades.client.render.flashbang

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.network.message.*
import club.pisquad.minecraft.csgrenades.registry.*
import club.pisquad.minecraft.csgrenades.sound.*
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.sounds.SoundEvents
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.time.Duration
import java.time.Instant
import kotlin.math.max
import kotlin.math.sqrt

private enum class RenderState {
    IDLE,
    AttackStage,
    SustainStage,
    DecayStage,
}

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object FlashbangBlindEffectRenderer {
    private var effectAttack: Int = 0
    private var effectSustain: Int = 0
    private var effectDecay: Int = 0
    private var effectAmount: Int = 0
    private var renderStartTime: Instant = Instant.now()
    private var renderState: RenderState = RenderState.IDLE

    fun render(effectData: FlashbangEffectData) {
        when (renderState) {
            RenderState.IDLE -> {
                renderStartTime = Instant.now()

                effectAttack = effectData.effectAttack
                effectSustain = effectData.effectSustain
                effectDecay = effectData.effectDecay
                effectAmount = effectData.effectAmount

                MinecraftForge.EVENT_BUS.register(FlashbangBlindEffectRenderer::eventHandler)
            }

            RenderState.AttackStage, RenderState.SustainStage -> {
                effectAttack = max(effectAttack, effectData.effectAttack)
                effectSustain = max(effectSustain, effectData.effectSustain)
                effectDecay = max(effectDecay, effectData.effectDecay)
                effectAmount = max(effectAmount, effectData.effectAmount)
            }

            RenderState.DecayStage -> {
                renderStartTime = Instant.now() - Duration.ofMillis(effectData.effectAttack.toLong())
                effectAttack = effectData.effectAttack
                effectSustain = effectData.effectSustain
                effectDecay = effectData.effectDecay
            }
        }
        playRingSound(effectData)
    }

    @JvmStatic
    @SubscribeEvent
    fun onPlaySound(event: PlaySoundEvent) {
        // If the player is not flashed, do nothing.
        if (renderState == RenderState.IDLE) {
            return
        }

        // Read the mutable property into a local immutable variable to allow smart casting.
        val sound = event.sound ?: return

        // Allow our custom ringing sound to play
        if (sound is FlashbangRingSound) {
            return
        }

        // Also allow the vanilla UI button click sound, it feels better.
        if (sound.location == SoundEvents.UI_BUTTON_CLICK.get().location) {
            return
        }

        // Allow the flashbang's own explosion sounds to play.
        val flashExplode = ModSoundEvents.FLASHBANG_EXPLODE.get()
        val flashExplodeDistant = ModSoundEvents.FLASHBANG_EXPLODE_DISTANT.get()
        if (sound.location == flashExplode.location || sound.location == flashExplodeDistant.location) {
            return
        }

        // Cancel all other sounds by replacing it with null
        event.sound = null
    }

    @JvmStatic
    @SubscribeEvent
    fun eventHandler(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_LEVEL) return

        val player = Minecraft.getInstance().player ?: return
        if (player.isSpectator) return

        val currentTime = Instant.now()
        // Converting type to double for precise calculation
        val timeDelta = Duration.between(renderStartTime, currentTime).toMillis().toDouble()

        val camera = event.camera
        val poseStack = event.poseStack

        if (timeDelta < effectAttack) {
            val opacity = (timeDelta / effectAttack * effectAmount).toInt()
            drawOverlay(camera, poseStack, opacity)
            renderState = RenderState.AttackStage
        } else if (timeDelta < effectSustain + effectAttack) {
            val opacity = (effectAmount)
            drawOverlay(camera, poseStack, opacity)
            renderState = RenderState.SustainStage
        } else if (timeDelta < effectDecay + effectSustain + effectAttack) {
            val opacity =
                effectAmount - ((timeDelta - effectAttack - effectSustain) / effectDecay * effectAmount).toInt()
            drawOverlay(camera, poseStack, opacity)
            renderState = RenderState.DecayStage
        } else {
            clean()
        }
        event.levelRenderer
    }

    private fun drawOverlay(camera: Camera, poseStack: PoseStack, opacity: Int) {
        poseStack.pushPose()
        poseStack.setIdentity()

        RenderSystem.disableDepthTest()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }

        0xFFFFFFFF.toInt()

        val matrix = poseStack.last().pose()

        val tesselator = Tesselator.getInstance()
        val buffer = tesselator.builder
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)

        buffer.vertex(matrix, -1.0f, -1.0f, -0.1f).color(255, 255, 255, opacity).endVertex()
        buffer.vertex(matrix, 1.0f, -1.0f, -0.1f).color(255, 255, 255, opacity).endVertex()
        buffer.vertex(matrix, 1.0f, 1.0f, -0.1f).color(255, 255, 255, opacity).endVertex()
        buffer.vertex(matrix, -1.0f, 1.0f, -0.1f).color(255, 255, 255, opacity).endVertex()

        BufferUploader.drawWithShader(buffer.end())

        RenderSystem.disableBlend()
        RenderSystem.enableDepthTest()

        poseStack.popPose()
    }

    private fun clean() {
        effectAttack = 0
        effectDecay = 0
        effectSustain = 0
        effectAmount = 0
        renderState = RenderState.IDLE
        MinecraftForge.EVENT_BUS.unregister(FlashbangBlindEffectRenderer::eventHandler)
    }

    private fun playRingSound(effectData: FlashbangEffectData) {
        val distance = sqrt(Minecraft.getInstance().player!!.distanceToSqr(effectData.position))

        Minecraft.getInstance().soundManager.play(
            FlashbangRingSound(
                attack = 0,
                sustain = effectSustain,
                decay = effectDecay + 300,
                targetVolume = SoundUtils.getVolumeFromDistance(distance, SoundTypes.FLASHBANG_RING).toFloat(),
            ),
        )
    }
}
