package club.pisquad.minecraft.csgrenades.grenades.flashbang.event

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.api.event.GrenadeActivatedEvent
import net.minecraft.world.phys.Vec3
import net.minecraftforge.fml.LogicalSide
import java.util.*

class FlashbangActivatedEvent(
    side: LogicalSide,
    ownerUUID: UUID,
    location: Vec3
) : GrenadeActivatedEvent(side, GrenadeType.FLASH_BANG, ownerUUID, location)