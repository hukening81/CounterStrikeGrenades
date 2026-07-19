package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.entity.runOnServer
import club.pisquad.minecraft.csgrenades.physics.GrenadeDuration
import club.pisquad.minecraft.csgrenades.physics.GrenadeHitBlock
import net.minecraft.core.Direction
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

abstract class FireGrenadeEntity(
    entityType: EntityType<out FireGrenadeEntity>, level: Level, maxLifeTime: Double
) :
    CounterStrikeGrenadeEntity(entityType, level) {


    val maxLifeTimeTick: Int = GrenadeDuration.fromSeconds(maxLifeTime).wholeTick

    override fun tick() {
        super.tick()

        this.runOnServer {
            if (this.tickCount > maxLifeTimeTick) {
                this.isStopped = true
                this.activate()
                this.popInAir()
            }
        }
    }

    override fun onHitBlock(data: GrenadeHitBlock) {
        super.onHitBlock(data)
        this.runOnServer {
            if (data.direction == Direction.UP) {
                this.isStopped = true
                this.activate()
                this.smashOnGround()
            }
        }
    }

    abstract fun popInAir()
    abstract fun smashOnGround()
}