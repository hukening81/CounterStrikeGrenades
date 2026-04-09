package club.pisquad.minecraft.csgrenades.core.entity.impl

import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level


/**
 * Abstract class for Smoke and Decoy
 * These only activate after landing
 * @param delay Grenade will activate after this amount of delay (in tick)
 * */
abstract class ActivateAfterLandingGrenadeEntity(
    pEntityType: EntityType<out ActivateAfterLandingGrenadeEntity>,
    pLevel: Level,
    val delay: Int,
) : CounterStrikeGrenadeEntity(pEntityType, pLevel) {
    var tickSinceLanding: Int = 0


    override fun tick() {
        super.tick()
        if (this.level().isClientSide) {
            // EMPTY
        } else {
            if (this.entityData.get(isActivatedAccessor)) {
                // EMPTY
            } else {
                if (this.entityData.get(isLandedAccessor)) {
                    if (tickSinceLanding > delay) {
                        this.activate()
                    }
                    tickSinceLanding++
                }
            }
        }
    }
}
