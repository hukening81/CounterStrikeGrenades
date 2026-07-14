package club.pisquad.minecraft.csgrenades.grenades.firegrenade.event

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.api.event.GrenadeActivatedEvent
import net.minecraftforge.fml.LogicalSide
import java.util.UUID

class FireGrenadeActivatedEvent(
    side: LogicalSide,
    grenadeType: GrenadeType,
    ownerUUID: UUID
) : GrenadeActivatedEvent(side, grenadeType, ownerUUID) {
}