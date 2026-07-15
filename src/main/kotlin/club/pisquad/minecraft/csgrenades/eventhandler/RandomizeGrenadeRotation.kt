package club.pisquad.minecraft.csgrenades.eventhandler

import club.pisquad.minecraft.csgrenades.api.event.GrenadeHitBlockEvent
import club.pisquad.minecraft.csgrenades.api.event.GrenadeHitEntityEvent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(Dist.CLIENT)
object RandomizeGrenadeRotation {

    @JvmStatic
    @SubscribeEvent
    @Suppress("unused")
    fun handleHitBlock(event: GrenadeHitBlockEvent) {
        event.grenade?.run {
            this.rotation.randomize()
        }
    }

    @JvmStatic
    @SubscribeEvent
    @Suppress("unused")
    fun handleHitEntity(event: GrenadeHitEntityEvent) {
        event.grenade?.run {
            this.rotation.randomize()
        }
    }
}