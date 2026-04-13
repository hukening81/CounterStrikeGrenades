package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.core.CSGrenadeEntityDataSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.BlockPosSerializer
import kotlinx.serialization.builtins.ListSerializer
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModSerializers {
    val blockPosListEntityDataSerializer = CSGrenadeEntityDataSerializer(
        ListSerializer(BlockPosSerializer())
    )

    @JvmStatic
    @SubscribeEvent
    fun register(event: FMLCommonSetupEvent) {
        EntityDataSerializers.registerSerializer(blockPosListEntityDataSerializer)
    }
}
