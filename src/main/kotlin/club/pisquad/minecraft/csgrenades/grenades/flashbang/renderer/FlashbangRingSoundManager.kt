package club.pisquad.minecraft.csgrenades.grenades.flashbang.renderer

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object FlashbangRingSoundManager