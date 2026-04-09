package club.pisquad.minecraft.csgrenades.api.event

import club.pisquad.minecraft.csgrenades.GrenadeType
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.fml.LogicalSide

abstract class CounterStrikeGrenadeEvent(
    val grenadeType: GrenadeType,
    val side: LogicalSide
) : Event()

class GrenadeActivationEvent(grenadeType: GrenadeType, side: LogicalSide) :
    CounterStrikeGrenadeEvent(grenadeType, side)

