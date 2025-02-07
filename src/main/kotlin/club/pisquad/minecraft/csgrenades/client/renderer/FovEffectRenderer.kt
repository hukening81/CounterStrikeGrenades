package club.pisquad.minecraft.csgrenades.client.renderer

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.FOV_EFFECT_AMOUNT
import club.pisquad.minecraft.csgrenades.STRONG_THROW_SPEED
import club.pisquad.minecraft.csgrenades.client.input.ThrowActionHandler
import club.pisquad.minecraft.csgrenades.linearInterpolate
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ComputeFovModifierEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object FovEffectRenderer {
    @SubscribeEvent
    fun onFovModifierEvent(event: ComputeFovModifierEvent) {
        if (ThrowActionHandler.currentThrowSpeed == null) {
            return
        }
        val throwSpeedFactor =
            ThrowActionHandler.currentThrowSpeed!! / STRONG_THROW_SPEED
        event.newFovModifier = event.fovModifier + linearInterpolate(0.0, FOV_EFFECT_AMOUNT, throwSpeedFactor).toFloat()

    }
}