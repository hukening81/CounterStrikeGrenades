package club.pisquad.minecraft.csgrenades.grenades.hegrenade

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.event.GrenadeActivateEvent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod


@Mod.EventBusSubscriber(
    modid = CounterStrikeGrenades.ID,
    bus = Mod.EventBusSubscriber.Bus.MOD,
    value = [Dist.DEDICATED_SERVER]
)
object HEGrenadeEventHandler {

    @JvmStatic
    @SubscribeEvent
    fun handler(event: GrenadeActivateEvent) {
        if (event.entity !is HEGrenadeEntity) {
            return
        }
    }
}
