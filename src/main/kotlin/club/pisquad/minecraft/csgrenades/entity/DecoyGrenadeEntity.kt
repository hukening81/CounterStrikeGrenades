package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.compat.tacz.TaczApiHandler
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registry.ModDamageType
import club.pisquad.minecraft.csgrenades.registry.ModItems
import club.pisquad.minecraft.csgrenades.simulation.DecoyFirePatternGenerator
import com.tacz.guns.api.TimelessAPI
import com.tacz.guns.api.item.IGun
import com.tacz.guns.api.item.gun.FireMode
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
import net.minecraftforge.fml.ModList

class DecoyGrenadeEntity(pEntityType: EntityType<out DecoyGrenadeEntity>, pLevel: Level) : CounterStrikeGrenadeEntity(pEntityType, pLevel, GrenadeType.DECOY_GRENADE) {

    private var fireTimestamps: List<Int> = emptyList()
    private var nextFireTimestampIndex: Int = 0
    private var activationTick: Int? = null

    companion object {
        private const val TOTAL_DURATION_TICKS = 15 * 20

        // Kept for data synchronization from thrower
        val CUSTOM_SOUND_ACCESSOR: EntityDataAccessor<String> = SynchedEntityData.defineId(DecoyGrenadeEntity::class.java, EntityDataSerializers.STRING)
        val SOUND_COUNTER_ACCESSOR: EntityDataAccessor<Int> = SynchedEntityData.defineId(DecoyGrenadeEntity::class.java, EntityDataSerializers.INT)
        val GUN_ID_TO_PLAY_ACCESSOR: EntityDataAccessor<String> = SynchedEntityData.defineId(DecoyGrenadeEntity::class.java, EntityDataSerializers.STRING)
        val GUN_FIRE_MODE_ACCESSOR: EntityDataAccessor<String> = SynchedEntityData.defineId(DecoyGrenadeEntity::class.java, EntityDataSerializers.STRING)
        val GUN_RPM_ACCESSOR: EntityDataAccessor<Int> = SynchedEntityData.defineId(DecoyGrenadeEntity::class.java, EntityDataSerializers.INT)
        val GUN_SHOOT_INTERVAL_MS_ACCESSOR: EntityDataAccessor<Int> = SynchedEntityData.defineId(DecoyGrenadeEntity::class.java, EntityDataSerializers.INT)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(CUSTOM_SOUND_ACCESSOR, "")
        entityData.define(SOUND_COUNTER_ACCESSOR, 0)
        entityData.define(GUN_ID_TO_PLAY_ACCESSOR, "")
        entityData.define(GUN_FIRE_MODE_ACCESSOR, "")
        entityData.define(GUN_RPM_ACCESSOR, 0)
        entityData.define(GUN_SHOOT_INTERVAL_MS_ACCESSOR, 0)
    }

    override fun getDefaultItem(): Item = ModItems.DECOY_GRENADE_ITEM.get()

    override fun tick() {
        super.tick() // This handles physics and rotation stopping on land

        if (level().isClientSide) {
            return
        }

        // Server-side logic
        if (entityData.get(isLandedAccessor) && activationTick == null) {
            activate()
        }

        activationTick?.let { startTick ->
            val currentTickInLifetime = tickCount - startTick
            // Check for next shot
            if (nextFireTimestampIndex < fireTimestamps.size) {
                val nextShotTick = fireTimestamps[nextFireTimestampIndex]
                if (currentTickInLifetime >= nextShotTick) {
                    fireShot()
                    nextFireTimestampIndex++
                }
            }

            // Check for end of life
            if (currentTickInLifetime > TOTAL_DURATION_TICKS) {
                endOfLifeExplosion()
            }
        }
    }

    override fun activate() {
        if (activationTick != null || level().isClientSide) return
        activationTick = this.tickCount
        this.fireTimestamps = DecoyFirePatternGenerator.generateFireTimestamps(this)
    }

    private fun fireShot() {
        val currentCounter = entityData.get(SOUND_COUNTER_ACCESSOR)
        entityData.set(SOUND_COUNTER_ACCESSOR, currentCounter + 1)
    }

    fun findAndSetTaczGunIdOnThrow() {
        if (!ModList.get().isLoaded("tacz")) return

        val owner = this.owner
        if (owner is Player) {
            // Find the first tacz gun in inventory
            owner.inventory.items.firstNotNullOfOrNull { itemStack ->
                (itemStack.item as? IGun)?.let { gun ->
                    val gunId = gun.getGunId(itemStack)
                    val fireMode = gun.getFireMode(itemStack)
                    val rpm = gun.getRPM(itemStack)
                    TimelessAPI.getCommonGunIndex(gunId).ifPresent { commonGunIndex ->
                        val gunData = commonGunIndex.gunData
                        val shootIntervalMs = gunData.getShootInterval(owner, fireMode, itemStack).toInt()
                        entityData.set(GUN_ID_TO_PLAY_ACCESSOR, gunId.toString())
                        entityData.set(GUN_FIRE_MODE_ACCESSOR, fireMode.name)
                        entityData.set(GUN_RPM_ACCESSOR, rpm)
                        entityData.set(GUN_SHOOT_INTERVAL_MS_ACCESSOR, shootIntervalMs)
                    }
                    gun // return non-null to stop searching
                }
            }
        }
    }

    fun setCustomSound(sound: String) {
        entityData.set(CUSTOM_SOUND_ACCESSOR, sound)
    }

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
