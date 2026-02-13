package club.pisquad.minecraft.csgrenades.entity.grenade

import club.pisquad.minecraft.csgrenades.event.GrenadeActivateEvent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.eventbus.api.SubscribeEvent

@OnlyIn(Dist.DEDICATED_SERVER)
object HEGrenadeEventHandler {
    @SubscribeEvent
    fun handler(event: GrenadeActivateEvent) {
        if (event.entity !is HEGrenadeEntity) {
            return
        }
    }
}
