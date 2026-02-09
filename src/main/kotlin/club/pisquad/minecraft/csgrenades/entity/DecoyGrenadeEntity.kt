package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.compat.tacz.TaczApiHandler
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registry.ModDamageType
import club.pisquad.minecraft.csgrenades.registry.ModItems
import com.tacz.guns.api.item.IGun
import com.tacz.guns.api.item.gun.FireMode
import net.minecraft.core.registries.Registries
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraftforge.fml.ModList
import kotlin.random.Random

class DecoyGrenadeEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) : CounterStrikeGrenadeEntity(pEntityType, pLevel, GrenadeType.DECOY_GRENADE) {

    // --- State Variables ---
    private var activationTick: Int? = null
    private var nextSoundTick: Int = 0
    private var lastSoundCounter = 0
    private var shotsInBurstRemaining = 0

    // For freezing rotation after activation
    private var hasSavedFinalRotation = false
    private var finalXRot = 0f
    private var finalYRot = 0f
    private var finalZRot = 0f

    companion object {
        private const val TOTAL_DURATION_TICKS = 15 * 20
        private const val SOUND_INTERVAL_BASE_TICKS = 1 * 20
        private const val SOUND_INTERVAL_RANDOM_TICKS = 2 * 20

        val CUSTOM_SOUND_ACCESSOR: EntityDataAccessor<String> = SynchedEntityData.defineId(
            DecoyGrenadeEntity::class.java,
            EntityDataSerializers.STRING,
        )
        val SOUND_COUNTER_ACCESSOR: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            DecoyGrenadeEntity::class.java,
            EntityDataSerializers.INT,
        )
        val GUN_ID_TO_PLAY_ACCESSOR: EntityDataAccessor<String> = SynchedEntityData.defineId(
            DecoyGrenadeEntity::class.java,
            EntityDataSerializers.STRING,
        )
        val GUN_FIRE_MODE_ACCESSOR: EntityDataAccessor<String> = SynchedEntityData.defineId(
            DecoyGrenadeEntity::class.java,
            EntityDataSerializers.STRING,
        )
        val GUN_RPM_ACCESSOR: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            DecoyGrenadeEntity::class.java,
            EntityDataSerializers.INT,
        )
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        this.entityData.define(CUSTOM_SOUND_ACCESSOR, "")
        this.entityData.define(SOUND_COUNTER_ACCESSOR, 0)
        this.entityData.define(GUN_ID_TO_PLAY_ACCESSOR, "")
        this.entityData.define(GUN_FIRE_MODE_ACCESSOR, "")
        this.entityData.define(GUN_RPM_ACCESSOR, 0)
    }

    override fun getDefaultItem(): Item = ModItems.DECOY_GRENADE_ITEM.get()

    override fun tick() {
        val isLanded = this.entityData.get(isLandedAccessor)

        if (isLanded) {
            // Decoy is on the ground, freeze rotation and handle sound/explosion logic
            if (level().isClientSide) {
                // Force-freeze rotation
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

                // Client-side sound playing
                val currentCounter = this.entityData.get(SOUND_COUNTER_ACCESSOR)
                if (currentCounter > lastSoundCounter) {
                    if (ModList.get().isLoaded("tacz")) {
                        val gunIdString = this.entityData.get(GUN_ID_TO_PLAY_ACCESSOR)
                        if (gunIdString.isNotBlank()) {
                            TaczApiHandler.playGunSound(this, ResourceLocation(gunIdString))
                        }
                    }
                    lastSoundCounter = currentCounter
                }
            } else { // Server-side
                if (activationTick == null) {
                    // First tick on the ground, activate the decoy
                    activationTick = this.tickCount
                    scheduleNextSound(true) // Schedule the very first sound
                } else {
                    // Logic for when the decoy is active
                    val currentActivationTick = tickCount - activationTick!!
                    if (tickCount >= nextSoundTick) {
                        // Time to make a sound
                        val fireMode = this.entityData.get(GUN_FIRE_MODE_ACCESSOR)
                        val rpm = this.entityData.get(GUN_RPM_ACCESSOR)

                        if (ModList.get().isLoaded("tacz") && this.entityData.get(GUN_ID_TO_PLAY_ACCESSOR).isNotBlank() && rpm > 0 && fireMode == FireMode.AUTO.name) {
                            // Auto-fire logic
                            if (shotsInBurstRemaining > 0) {
                                fireShot()
                                shotsInBurstRemaining--
                                if (shotsInBurstRemaining > 0) {
                                    val delay = (60 * 20 / rpm).coerceAtLeast(1)
                                    nextSoundTick = tickCount + delay
                                } else {
                                    // Last shot of the burst, schedule next burst
                                    scheduleNextSound(true)
                                }
                            } else {
                                // Start a new burst
                                shotsInBurstRemaining = Random.nextInt(3, 6) // 3-5 shots per burst
                                fireShot()
                                shotsInBurstRemaining--
                                val delay = (60 * 20 / rpm).coerceAtLeast(1)
                                nextSoundTick = tickCount + delay
                            }
                        } else {
                            // Semi-auto logic (or fallback)
                            fireShot()
                            scheduleNextSound(false)
                        }
                    }

                    if (currentActivationTick > TOTAL_DURATION_TICKS) {
                        endOfLifeExplosion()
                    }
                }
            }
        } else {
            // Decoy is in the air/not yet active, run full physics
            super.tick()
        }
    }

    fun findAndSetTaczGunIdOnThrow() {
        if (!ModList.get().isLoaded("tacz")) {
            return
        }
        val owner = this.owner
        if (owner is Player) {
            for (itemStack in owner.inventory.items) {
                val item = itemStack.item
                if (item is IGun) {
                    val gunId = item.getGunId(itemStack)
                    val fireMode = item.getFireMode(itemStack)
                    val rpm = item.getRPM(itemStack)
                    this.entityData.set(GUN_ID_TO_PLAY_ACCESSOR, gunId.toString())
                    this.entityData.set(GUN_FIRE_MODE_ACCESSOR, fireMode.name)
                    this.entityData.set(GUN_RPM_ACCESSOR, rpm)
                    return // Found a gun, exit
                }
            }
        }
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

    // This method is called from the server-side network handler
    fun setCustomSound(sound: String) {
        this.entityData.set(CUSTOM_SOUND_ACCESSOR, sound)
    }

    private fun scheduleNextSound(isFirstBurst: Boolean) {
        val longDelay = SOUND_INTERVAL_BASE_TICKS + Random.nextInt(SOUND_INTERVAL_RANDOM_TICKS)
        nextSoundTick = if (isFirstBurst) {
            // shorter delay for the very first sound
            tickCount + Random.nextInt(SOUND_INTERVAL_BASE_TICKS)
        } else {
            tickCount + longDelay
        }
    }

    private fun fireShot() {
        if (ModList.get().isLoaded("tacz") && this.entityData.get(GUN_ID_TO_PLAY_ACCESSOR).isNotBlank()) {
            val currentCounter = this.entityData.get(SOUND_COUNTER_ACCESSOR)
            this.entityData.set(SOUND_COUNTER_ACCESSOR, currentCounter + 1)
        } else {
            playSoundLogic()
        }
    }

    private fun playSoundLogic() {
        val customSound = this.entityData.get(CUSTOM_SOUND_ACCESSOR)

        if (customSound.isNotBlank()) {
            // Play the custom sound from NBT
            try {
                val soundEvent = SoundEvent.createVariableRangeEvent(ResourceLocation(customSound))
                level().playSound(null, this.x, this.y, this.z, soundEvent, SoundSource.PLAYERS, 4.0f, 1.0f)
            } catch (e: Exception) {
                // If the custom sound name is invalid, fall back to default
                playDefaultSound()
            }
        } else {
            // Fallback to default random footstep sounds
            playDefaultSound()
        }
    }

    private fun playDefaultSound() {
        val footstepSounds = arrayOf(
            SoundEvents.CREEPER_HURT,
            SoundEvents.CREEPER_DEATH,
            SoundEvents.CREEPER_PRIMED,
//            SoundEvents.FIREWORK_ROCKET_BLAST,
            SoundEvents.CHICKEN_HURT,
            SoundEvents.CHICKEN_AMBIENT,
        )
        val randomSoundHolder = footstepSounds[Random.nextInt(footstepSounds.size)]
        level().playSound(null, this.blockPosition(), randomSoundHolder, SoundSource.PLAYERS, 1.0f, 1.0f)
    }

    private fun endOfLifeExplosion() {
        if (!level().isClientSide) {
            this.level().explode(this, this.x, this.y, this.z, 0.1f, false, Level.ExplosionInteraction.NONE)
        }
        this.discard()
    }
}
