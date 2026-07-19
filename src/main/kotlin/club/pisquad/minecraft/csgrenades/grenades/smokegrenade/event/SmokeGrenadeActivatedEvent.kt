package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.event

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.api.event.GrenadeActivatedEvent
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelMap
import net.minecraft.world.phys.Vec3
import net.minecraftforge.fml.LogicalSide
import java.util.*

class SmokeGrenadeActivatedEvent(
    side: LogicalSide,
    ownerUUID: UUID,
    val grenade: CounterStrikeGrenadeEntity?,
    val voxels: VoxelMap,
    location: Vec3,
) : GrenadeActivatedEvent(side, GrenadeType.SMOKE_GRENADE, ownerUUID, location)