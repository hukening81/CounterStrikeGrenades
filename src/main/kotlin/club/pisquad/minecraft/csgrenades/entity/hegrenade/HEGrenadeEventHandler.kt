package club.pisquad.minecraft.csgrenades.entity.hegrenade

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.event.*
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@OnlyIn(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.DEDICATED_SERVER])
object HEGrenadeEventHandler {

    @JvmStatic
    @SubscribeEvent
    fun handler(event: GrenadeActivateEvent) {
        if (event.entity !is HEGrenadeEntity) {
            return
        }
    }
}
