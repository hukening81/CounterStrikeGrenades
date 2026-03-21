package club.pisquad.minecraft.csgrenades.client

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GrenadeType
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod


@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ModClientEvents {

    @JvmStatic
    @SubscribeEvent
    fun onModelsRegistered(event: ModelEvent.RegisterAdditional) {
        val keys = GrenadeType.entries.map { it.resourceKey }

        keys.forEach { key ->
            val thrownModelLocation = ResourceLocation(CounterStrikeGrenades.ID, "item/${key}_t")
            event.register(thrownModelLocation)
        }
    }
}
