package club.pisquad.minecraft.csgrenades.client.render

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ComputeFovModifierEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object FovEffectRenderer {

    @JvmStatic
    @SubscribeEvent
    fun onFovModifierEvent(event: ComputeFovModifierEvent) {
//        val strength = InputHandler.getCurrentStrength() ?: return
//        event.newFovModifier =
//            event.fovModifier + Mth.lerp(
//                strength.div(3.0),
//                0.0,
//                ModConfig.throwConfig.fov_effect_amount.get(),
//            ).toFloat()
    }
}
