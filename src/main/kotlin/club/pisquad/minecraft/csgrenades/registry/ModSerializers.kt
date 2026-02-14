package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.entity.firegrenade.BlockPosListEntityDataSerializer
import club.pisquad.minecraft.csgrenades.entity.smokegrenade.*
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModSerializers {
    val smokeDataPointSetSerializer = SmokeDataPointSetEntityDataSerializer()
    val blockPosListEntityDataSerializer = BlockPosListEntityDataSerializer()

    @JvmStatic
    @SubscribeEvent
    fun register(event: FMLCommonSetupEvent) {
        EntityDataSerializers.registerSerializer(smokeDataPointSetSerializer)
        EntityDataSerializers.registerSerializer(blockPosListEntityDataSerializer)
    }
}
