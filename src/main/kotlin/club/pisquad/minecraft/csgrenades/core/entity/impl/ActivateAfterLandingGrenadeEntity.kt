package club.pisquad.minecraft.csgrenades.core.entity.impl

import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.runOnServer
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
    var tickSinceLanded: Int = 0

    override fun tick() {
        super.tick()
        this.runOnServer {
            if (this.isStopped) {
                tickSinceLanded++
            } else if (tickSinceLanded != 0) {
                tickSinceLanded = 0
            }

            if (tickSinceLanded == delay && !this.isActivated) {
                this.activate()
            }
        }
    }
}
