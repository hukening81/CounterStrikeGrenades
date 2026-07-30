package club.pisquad.minecraft.csgrenades.api.event

import club.pisquad.minecraft.csgrenades.GrenadeType
import net.minecraftforge.fml.LogicalSide
import java.util.*

open class CSGrenadeServerSideEvent(
    grenadeType: GrenadeType, ownerUUID: UUID
) : CSGrenadeEvent(LogicalSide.SERVER, grenadeType, ownerUUID)

