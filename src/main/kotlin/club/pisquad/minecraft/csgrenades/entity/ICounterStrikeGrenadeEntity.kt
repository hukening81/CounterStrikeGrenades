package club.pisquad.minecraft.csgrenades.entity

import net.minecraft.world.phys.Vec3
import java.util.UUID

interface ICounterStrikeGrenadeEntity {
    var ownerUuid: UUID
    val center: Vec3
    val velocity: Vec3
    fun initialize(ownerUuid:UUID,position:Vec3,velocity: Vec3)
}
