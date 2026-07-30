package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.core.entity.impl.ActivateAfterLandingGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.entity.runOnServer
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelWorker
import club.pisquad.minecraft.csgrenades.toTick
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
            val voxelMap = voxelWorker!!.blockingUntilComplete()
            ModLogger.info(this) { "Voxel calculation done, none empty voxel count:{}".format(voxelMap.size) }
        }
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