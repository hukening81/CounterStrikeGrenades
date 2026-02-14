package club.pisquad.minecraft.csgrenades.entity.smokegrenade

import club.pisquad.minecraft.csgrenades.*
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.common.Mod

@OnlyIn(Dist.DEDICATED_SERVER)
@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.DEDICATED_SERVER])
object SmokeGrenadeEventHandler
