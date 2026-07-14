package club.pisquad.minecraft.csgrenades.grenades.flashbang.event

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.api.event.GrenadeActivatedEvent
import net.minecraftforge.fml.LogicalSide
import java.util.UUID

class FlashbangActivatedEvent(
    side: LogicalSide,
    ownerUUID: UUID
) : GrenadeActivatedEvent(side, GrenadeType.FLASH_BANG, ownerUUID) {
}