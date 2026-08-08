package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.core.entity.HitBlockHandleResult
import club.pisquad.minecraft.csgrenades.core.entity.impl.ActivateAfterLandingGrenadeEntity
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireRegionEntity
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelWorker
import club.pisquad.minecraft.csgrenades.physics.GrenadeHitSomething
import club.pisquad.minecraft.csgrenades.runOnServer
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

abstract class SmokeGrenadeEntity(pEntityType: EntityType<out SmokeGrenadeEntity>, pLevel: Level) :
    ActivateAfterLandingGrenadeEntity(
        pEntityType,
        pLevel,
        SmokeGrenadeConfig.common.fuseTime.get().toTick().toInt(),
    ) {
    abstract val variant: SmokeGrenadeVariant

    private var voxelWorker: VoxelWorker? = null

    override val sounds = SmokeGrenadeSounds
    override val damageTypes = SmokeGrenadeDamageTypes

    override fun defineSynchedData() {
        super.defineSynchedData()
    }

    override fun onStopped() {
        super.onStopped()
        this.runOnServer {
            voxelWorker = VoxelWorker(this)
        }
    }

    override fun activate() {
        super.activate()
        this.runOnServer {
            if (this.voxelWorker == null) {
                this.voxelWorker = VoxelWorker(this)
            }
            val voxelMap =
                voxelWorker!!.blockingUntilComplete(SmokeGrenadeOptions.voxelDebugMode != VoxelDebugMode.NONE)
            ModLogger.info(this) { "Voxel calculation done, none empty voxel count:{}".format(voxelMap.size) }
            SmokeGrenadeUtils.spawnSmokeRegionEntity(
                this.level() as ServerLevel,
                this.ownerUuid,
                this.variant,
                voxelMap,
            )
            if (!this.isStopped) {
                this.isStopped = true
            }
            this.discard()
        }
    }

    override fun onHitBlock(data: GrenadeHitSomething.GrenadeHitBlock): HitBlockHandleResult {
        val handleResult = super.onHitBlock(data)
        this.runOnServer {
            if (data.direction == Direction.UP) {
                val adjustedHitPoint = data.hitPoint.add(0.0, 0.1, 0.0)

                val shouldActivate = FireRegionEntity.serverTrackedEntities.get(this.level().dimension())?.any() {
                    it.boundingBox.contains(adjustedHitPoint) && it.flameMap.keys.any() { it.contains(adjustedHitPoint) }
                }?:false

                if (shouldActivate) {
                    this.activate()
                    handleResult.shouldStop = true
                    handleResult.shouldPlaySound = false
                }
            }
        }
        return handleResult
    }
}

class TSmokeGrenadeEntity(entity: EntityType<out TSmokeGrenadeEntity>, level: Level) :
    SmokeGrenadeEntity(entity, level) {
    override val grenadeType: GrenadeType = GrenadeType.T_SMOKE
    override val variant: SmokeGrenadeVariant = SmokeGrenadeVariant.T
}

class CTSmokeGrenadeEntity(entity: EntityType<out CTSmokeGrenadeEntity>, level: Level) :
    SmokeGrenadeEntity(entity, level) {
    override val grenadeType: GrenadeType = GrenadeType.CT_SMOKE
    override val variant: SmokeGrenadeVariant = SmokeGrenadeVariant.CT
}