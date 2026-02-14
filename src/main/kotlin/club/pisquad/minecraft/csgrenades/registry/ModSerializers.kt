package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.serializer.*
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object ModSerializers {
    val blockPosListEntityDataSerializer = BlockPosListEntityDataSerializer()

    @JvmStatic
    @SubscribeEvent
    fun register(event: FMLCommonSetupEvent) {
        EntityDataSerializers.registerSerializer(blockPosListEntityDataSerializer)
    }
}
