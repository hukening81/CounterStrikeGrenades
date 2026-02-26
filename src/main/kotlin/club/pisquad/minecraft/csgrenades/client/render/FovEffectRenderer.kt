package club.pisquad.minecraft.csgrenades.client.render

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.client.input.InputState
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.linearInterpolate
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ComputeFovModifierEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object FovEffectRenderer {

    @JvmStatic
    @SubscribeEvent
    fun onFovModifierEvent(event: ComputeFovModifierEvent) {
        if (InputState.idle) {
            return
        }
        event.newFovModifier =
            event.fovModifier + linearInterpolate(0.0, ModConfig.FOV_EFFECT_AMOUNT.get(), InputState.strength.div(3)).toFloat()
    }
}
