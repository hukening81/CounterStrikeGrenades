package club.pisquad.minecraft.csgrenades.api.event

import club.pisquad.minecraft.csgrenades.GrenadeType
import net.minecraftforge.eventbus.api.Cancelable
import net.minecraftforge.fml.LogicalSide
import java.util.*

open class CSGrenadeClientSideEvent(
    grenadeType: GrenadeType, ownerUUID: UUID
) : CSGrenadeEvent(LogicalSide.CLIENT, grenadeType, ownerUUID)

@Cancelable
open class ClientGrenadeHitBlockSoundEvent(
    grenadeType: GrenadeType, ownerUUID: UUID
) : CSGrenadeClientSideEvent(grenadeType, ownerUUID)


@Cancelable
open class ClientGrenadeHitEntitySoundEvent(
    grenadeType: GrenadeType, ownerUUID: UUID
) : CSGrenadeClientSideEvent(grenadeType, ownerUUID)

