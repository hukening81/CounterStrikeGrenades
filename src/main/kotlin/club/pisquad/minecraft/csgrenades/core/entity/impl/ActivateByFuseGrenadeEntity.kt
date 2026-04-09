package club.pisquad.minecraft.csgrenades.core.entity.impl

import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
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
    val fuseTime: Int,
) : CounterStrikeGrenadeEntity(pEntityType, pLevel) {

    override fun tick() {
        super.tick()
        if (!this.entityData.get(isActivatedAccessor) && this.tickCount > fuseTime) {
            this.activate()
        }
    }
}
