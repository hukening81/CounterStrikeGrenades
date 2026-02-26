package club.pisquad.minecraft.csgrenades.event

import club.pisquad.minecraft.csgrenades.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import net.minecraftforge.event.entity.EntityEvent

/**
 * Fires when a grenade activates.
 *
 * Only Fires on server side
 *
 * by *activate* we mean *explode* for most of the grenades
 * */
class GrenadeActivateEvent(
    val entity: CounterStrikeGrenadeEntity,
    val grenadeType: GrenadeType,
) : EntityEvent(entity)
