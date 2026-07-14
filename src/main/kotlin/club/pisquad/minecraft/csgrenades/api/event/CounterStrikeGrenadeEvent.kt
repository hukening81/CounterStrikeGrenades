package club.pisquad.minecraft.csgrenades.api.event

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.physics.GrenadeHitBlock
import club.pisquad.minecraft.csgrenades.physics.GrenadeHitEntity
import club.pisquad.minecraft.csgrenades.physics.GrenadeVelocity
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.fml.LogicalSide
import java.util.UUID

open class CounterStrikeGrenadeEvent(
    val side: LogicalSide,
    val grenadeType: GrenadeType,
    val ownerUUID: UUID,
) : Event()

open class GrenadeActivatedEvent(
    side: LogicalSide,
    grenadeType: GrenadeType,
    ownerUUID: UUID,
) :
    CounterStrikeGrenadeEvent(side, grenadeType, ownerUUID)

open class GrenadeHitBlockEvent(
    side: LogicalSide,
    grenadeType: GrenadeType,
    ownerUUID: UUID,
    val grenade: CounterStrikeGrenadeEntity?,
    val blockPos: BlockPos,
    val hitPoint: Vec3,
    val velocity: GrenadeVelocity,
) : CounterStrikeGrenadeEvent(side, grenadeType, ownerUUID) {
    companion object {
        fun create(
            side: LogicalSide,
            grenade: CounterStrikeGrenadeEntity,
            data: GrenadeHitBlock
        ): GrenadeHitBlockEvent {
            return GrenadeHitBlockEvent(
                side,
                grenade.grenadeType,
                grenade.ownerUuid,
                grenade,
                data.blockPos,
                data.hitPoint,
                data.velocity
            )
        }
    }
}

open class GrenadeHitEntityEvent(
    side: LogicalSide,
    grenadeType: GrenadeType,
    ownerUUID: UUID,
    val grenade: CounterStrikeGrenadeEntity?,
    val entity: Entity?,
    val hitPoint: Vec3,
    val velocity: GrenadeVelocity,
) : CounterStrikeGrenadeEvent(side, grenadeType, ownerUUID) {
    companion object {
        fun create(
            side: LogicalSide,
            grenade: CounterStrikeGrenadeEntity,
            data: GrenadeHitEntity
        ): GrenadeHitEntityEvent {
            return GrenadeHitEntityEvent(
                side,
                grenade.grenadeType,
                grenade.ownerUuid,
                grenade,
                data.entity,
                data.hitPoint,
                data.velocity
            )
        }
    }
}