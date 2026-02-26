package club.pisquad.minecraft.csgrenades.entity.core

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

/**
 * Abstract class for HE Grenade and Fire Grenade
 * These grenades activate after certain amount of time
 * @param fuseTime Grenade will activate after this amount of delay (in tick)
 * */
abstract class ActivateByFuseGrenadeEntity(
    pEntityType: EntityType<out ActivateByFuseGrenadeEntity>,
    pLevel: Level,
    grenadeType: GrenadeType,
    val fuseTime: Int,
) : CounterStrikeGrenadeEntity(pEntityType, pLevel, grenadeType) {
    // Can we use entity.tickCount here?
    var tickSinceSpawn: Int = 0

    override fun tick() {
        super.tick()
        tickSinceSpawn++
        if (!this.entityData.get(isActivatedAccessor) && tickSinceSpawn > fuseTime) {
            this.activate()
        }
    }
}
