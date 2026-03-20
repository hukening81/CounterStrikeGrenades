package club.pisquad.minecraft.csgrenades.entity.smokegrenade

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.entity.core.ActivateAfterLandingGrenadeEntity
import club.pisquad.minecraft.csgrenades.entity.core.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.entity.core.GrenadeEntitySoundEvents
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registry.ModSerializers
import club.pisquad.minecraft.csgrenades.registry.damage.ModDamageTypes
import club.pisquad.minecraft.csgrenades.registry.sounds.SmokeGrenadeSoundEvents
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
    override val sounds = GrenadeEntitySoundEvents(
        SmokeGrenadeSoundEvents.throwSound.get(),
        SmokeGrenadeSoundEvents.bounce.get(),
    )
    override val damageTypes = GrenadeEntityDamageTypes(
        ModDamageTypes.smokegrenade.hit,
        ModDamageTypes.smokegrenade.hit,
    )

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
