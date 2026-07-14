package club.pisquad.minecraft.csgrenades.grenades.hegrenade.event

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.api.event.GrenadeActivatedEvent
import net.minecraftforge.fml.LogicalSide
import java.util.UUID

class HEGrenadeActivatedEvent(
    side: LogicalSide,
    ownerUUID: UUID
) : GrenadeActivatedEvent(side, GrenadeType.HE_GRENADE, ownerUUID) {
}