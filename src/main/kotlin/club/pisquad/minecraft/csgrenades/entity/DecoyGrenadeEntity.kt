package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.compat.tacz.TaczGunDataProvider
import club.pisquad.minecraft.csgrenades.entity.decoy.DecoyGunData
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.DecoyShotMessage
import club.pisquad.minecraft.csgrenades.registry.ModDamageType
import club.pisquad.minecraft.csgrenades.registry.ModItems
import club.pisquad.minecraft.csgrenades.simulation.DecoyFirePatternGenerator
import net.minecraft.core.registries.Registries
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraftforge.network.PacketDistributor

class DecoyGrenadeEntity(pEntityType: EntityType<out DecoyGrenadeEntity>, pLevel: Level) : CounterStrikeGrenadeEntity(pEntityType, pLevel, GrenadeType.DECOY_GRENADE) {

    private var fireTimestamps: List<Int> = emptyList()
    private var nextFireTimestampIndex: Int = 0
    private var activationTick: Int? = null
    private var gunData: DecoyGunData? = null
    private var customSound: String? = null

    private var hasSavedFinalRotation = false
    private var finalXRot = 0f
    private var finalYRot = 0f
    private var finalZRot = 0f

    companion object {
        private const val TOTAL_DURATION_TICKS = 15 * 20

        val GUN_DATA_ACCESSOR: EntityDataAccessor<String> = SynchedEntityData.defineId(DecoyGrenadeEntity::class.java, EntityDataSerializers.STRING)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(GUN_DATA_ACCESSOR, "")
    }

    override fun getDefaultItem(): Item = ModItems.DECOY_GRENADE_ITEM.get()

    override fun tick() {
        if (!entityData.get(isLandedAccessor)) {
            super.tick()
            return
        }

        if (level().isClientSide) {
            freezeRotation()
            return
        }

        if (activationTick == null) {
            activate()
        }

        activationTick?.let { startTick ->
            val currentTickInLifetime = tickCount - startTick
            if (nextFireTimestampIndex < fireTimestamps.size) {
                val nextShotTick = fireTimestamps[nextFireTimestampIndex]
                if (currentTickInLifetime >= nextShotTick) {
                    fireShot()
                    nextFireTimestampIndex++
                }
            }

            if (currentTickInLifetime > TOTAL_DURATION_TICKS) {
                endOfLifeExplosion()
            }
        }
    }

    private fun freezeRotation() {
        if (!hasSavedFinalRotation) {
            finalXRot = this.customXRot
            finalYRot = this.customYRot
            finalZRot = this.customZRot
            hasSavedFinalRotation = true
        }
        this.customXRot = finalXRot
        this.customYRot = finalYRot
        this.customZRot = finalZRot
        this.customXRotO = finalXRot
        this.customYRotO = finalYRot
        this.customZRotO = finalZRot
    }

    override fun activate() {
        if (activationTick != null || level().isClientSide) return
        activationTick = this.tickCount
        this.fireTimestamps = DecoyFirePatternGenerator.generateFireTimestamps(this)
    }

    private fun fireShot() {
        val gunId = gunData?.gunId ?: return
        CsGrenadePacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), DecoyShotMessage(this.id, gunId, customSound))
    }

    fun findAndSetTaczGunIdOnThrow() {
        val owner = this.owner
        if (owner is Player) {
            gunData = TaczGunDataProvider.findGunDataFromPlayer(owner)
            gunData?.let { data ->
                entityData.set(GUN_DATA_ACCESSOR, data.gunId)
            }
        }
    }

    fun setCustomSound(sound: String) {
        customSound = sound
    }

    fun getGunData(): DecoyGunData? = gunData

    private fun endOfLifeExplosion() {
        if (!level().isClientSide) {
            this.level().explode(this, this.x, this.y, this.z, 0.1f, false, Level.ExplosionInteraction.NONE)
        }
        this.discard()
    }

    override fun getHitDamageSource(hitEntity: LivingEntity): DamageSource {
        val registryAccess = this.level().registryAccess()
        val damageTypeHolder = registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.DECOY_GRENADE_HIT)
        return if (hitEntity == this.owner) {
            DamageSource(damageTypeHolder, this)
        } else {
            DamageSource(damageTypeHolder, this, this.owner)
        }
    }
}
