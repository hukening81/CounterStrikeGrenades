package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.core.entity.impl.ActivateAfterLandingGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.entity.runOnServer
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelMap
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelWorker
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class SmokeGrenadeEntity(pEntityType: EntityType<out SmokeGrenadeEntity>, pLevel: Level) :
    ActivateAfterLandingGrenadeEntity(
        pEntityType,
        pLevel,
        SmokeGrenadeConfig.common.fuseTime.get().toTick().toInt(),
    ) {
    private var voxelWorker: VoxelWorker? = null

    override val sounds = SmokeGrenadeRegistries.sounds
    override val damageTypes = SmokeGrenadeRegistries.damageTypes
    override val grenadeType: GrenadeType = GrenadeType.SMOKE_GRENADE

    val voxels: VoxelMap
        get() {
            return this.entityData.get(voxelMapAccessor)
        }

    companion object {
        val voxelMapAccessor: EntityDataAccessor<VoxelMap> =
            SynchedEntityData.defineId(
                SmokeGrenadeEntity::class.java,
                SmokeGrenadeRegistries.serializers.voxelMapSerializer
            )
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        this.entityData.define(voxelMapAccessor, VoxelMap.EMPTY)
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