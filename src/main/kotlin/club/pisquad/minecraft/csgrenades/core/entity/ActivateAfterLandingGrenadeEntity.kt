package club.pisquad.minecraft.csgrenades.core.entity

import club.pisquad.minecraft.csgrenades.GrenadeType
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3


/**
 * Abstract class for Smoke and Decoy
 * These only activate after landing
 * @param delay Grenade will activate after this amount of delay (in tick)
 * */
abstract class ActivateAfterLandingGrenadeEntity(
    pEntityType: EntityType<out ActivateAfterLandingGrenadeEntity>,
    pLevel: Level,
    grenadeType: GrenadeType,
    val delay: Int,
) : CounterStrikeGrenadeEntity(pEntityType, pLevel, grenadeType) {
    var tickSinceLanding: Int = 0

    companion object {
        val isLandedAccessor: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(
            ActivateAfterLandingGrenadeEntity::class.java,
            EntityDataSerializers.BOOLEAN
        )
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        this.entityData.define(isLandedAccessor, false)
    }

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
                } else {
                    // super.onHitBlock() contains a mechanism to freeze the entity after landing on groud
                    // we rely on that the check if we are landed
                    if (this.deltaMovement == Vec3.ZERO) {
                        this.entityData.set(isLandedAccessor, true)
                    }
                }
            }
        }
    }
}
