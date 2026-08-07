package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.entity.HitBlockHandleResult
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.flame.FlameSpreader
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.messages.ActivateReason
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.messages.ServerFireGrenadeActivatedMessage
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeRegionEntity
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.physics.GrenadeDuration
import club.pisquad.minecraft.csgrenades.physics.GrenadeHitBlock
import club.pisquad.minecraft.csgrenades.physics.GrenadePosition
import club.pisquad.minecraft.csgrenades.runOnServer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

abstract class FireGrenadeEntity(
    entityType: EntityType<out FireGrenadeEntity>, level: Level, val fuseTime: Double
) :
    CounterStrikeGrenadeEntity(entityType, level) {

    abstract val variant: FireGrenadeVariant

    val maxLifeTimeTick: Int = GrenadeDuration.fromSeconds(fuseTime).wholeTick

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

    override fun onHitBlock(data: GrenadeHitBlock): HitBlockHandleResult {
        val result = super.onHitBlock(data)
        this.runOnServer {
            if (data.direction == Direction.UP) {
                this.setPos(
                    GrenadePosition.fromCenter(data.hitPoint).worldPos
                )
                this.isStopped = true
                this.activate()
                val offsetLocation = data.hitPoint.add(0.0, 0.1, 0.0)
                if (isPositionInSmoke(this.level() as ServerLevel, offsetLocation)) {
                    this.smashInSmoke()
                } else {
                    this.smashOnGround(offsetLocation)
                }
                this.discard()
            }
        }
        return result
    }

    fun popInAir() {
        this.runOnServer {
            val message = ServerFireGrenadeActivatedMessage(this.id, ActivateReason.PopInAir(this.center))
            ModPacketHandler.sendMessageToPlayer(this.level() as ServerLevel, this.center, message)
        }
    }

    fun smashOnGround(location: Vec3) {
        ModLogger.debug(this) {
            val blockState = this.level().getBlockState(BlockPos.containing(location))
            " smashed on ground at block [${blockState.block.name}]"
        }

        this.runOnServer {
            val flameMap = FlameSpreader(location, 3.0, 5).spread(this.level())
            FireRegionEntity.create(
                this.level() as ServerLevel, this.center, this.variant, flameMap,
                FireGrenadeOptions.debugMode
            )

            val message =
                ServerFireGrenadeActivatedMessage(this.id, ActivateReason.SmashOnGround(this.center, flameMap))
            ModPacketHandler.sendMessageToPlayer(this.level() as ServerLevel, this.center, message)
        }
    }

    fun smashInSmoke() {
        this.runOnServer {
            val message = ServerFireGrenadeActivatedMessage(this.id, ActivateReason.SmashInSmoke(this.center))
            ModPacketHandler.sendMessageToPlayer(this.level() as ServerLevel, this.center, message)
        }
    }
}

private fun isPositionInSmoke(level: ServerLevel, center: Vec3): Boolean {
    return SmokeRegionEntity.trackedRegions.filter { it.boundingBox.contains(center) }.filter {
        it.voxelMap.any() { it.key.contains(center) }
    }.isNotEmpty()
}