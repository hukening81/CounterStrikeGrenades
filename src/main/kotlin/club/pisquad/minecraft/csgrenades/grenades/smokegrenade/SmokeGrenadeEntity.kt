package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.entity.ActivateAfterLandingGrenadeEntity
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils.SmokeDataPoint
import club.pisquad.minecraft.csgrenades.registry.ModSerializers
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class SmokeGrenadeEntity(pEntityType: EntityType<out SmokeGrenadeEntity>, pLevel: Level) :
    ActivateAfterLandingGrenadeEntity(
        pEntityType,
        pLevel,
        GrenadeType.SMOKE_GRENADE,
        ModConfig.smokegrenade.grenadeCommonConfig.fuseTime.get().toTick().toInt(),
    ) {
    override val sounds = SmokeGrenadeRegistries.sounds
    override val damageTypes = SmokeGrenadeRegistries.damageTypes


    companion object {
        private val smokeDataPointsAccessor: EntityDataAccessor<Set<SmokeDataPoint>> =
            SynchedEntityData.defineId(SmokeGrenadeEntity::class.java, ModSerializers.smokeDataPointSetSerializer)

    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        this.entityData.define(smokeDataPointsAccessor, emptySet())
    }


    fun getSmokeDataPoints(): Set<SmokeDataPoint> = this.entityData.get(smokeDataPointsAccessor)
}
