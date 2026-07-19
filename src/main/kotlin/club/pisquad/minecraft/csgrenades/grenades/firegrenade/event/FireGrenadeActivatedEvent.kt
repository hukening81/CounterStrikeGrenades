package club.pisquad.minecraft.csgrenades.grenades.firegrenade.event

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.api.event.GrenadeActivatedEvent
import net.minecraft.world.phys.Vec3
import net.minecraftforge.fml.LogicalSide
import java.util.*

class FireGrenadeActivatedEvent(
    side: LogicalSide,
    grenadeType: GrenadeType,
    ownerUUID: UUID,
    location: Vec3
) : GrenadeActivatedEvent(side, grenadeType, ownerUUID, location)