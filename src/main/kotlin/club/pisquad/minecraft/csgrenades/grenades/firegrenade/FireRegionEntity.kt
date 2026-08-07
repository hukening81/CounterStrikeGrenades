package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.grenades.firegrenade.flame.FlameMap
import club.pisquad.minecraft.csgrenades.hurtCancelKnockback
import club.pisquad.minecraft.csgrenades.physics.GrenadeDuration
import club.pisquad.minecraft.csgrenades.runOnClient
import club.pisquad.minecraft.csgrenades.runOnServer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraftforge.entity.IEntityAdditionalSpawnData
import net.minecraftforge.network.NetworkHooks
import kotlin.math.min
import kotlin.math.roundToInt


class FireRegionEntity(entityType: EntityType<FireRegionEntity>, level: Level) : Entity(entityType, level),
    IEntityAdditionalSpawnData {
    var hasInitialized: Boolean = false
    var debugMode: Boolean = false
    lateinit var variant: FireGrenadeVariant
    lateinit var flameMap: FlameMap
    lateinit var fireLoopSoundInstance: SimpleSoundInstance
    var lifetime = -1

    // (Entity's ID, tick time when it enters the damage region)
    var entityPreviousInRange: MutableMap<Int, Int> = mutableMapOf()

    init {
        noPhysics = true
    }

    companion object {
        val clientTrackedEntities: MutableSet<FireRegionEntity> = mutableSetOf()
        val serverTrackedEntities: MutableSet<FireRegionEntity> = mutableSetOf()

        val FIRE_FADEOUT_SOUND_LENGTH_TICK: Int = GrenadeDuration.fromSeconds(1.28).ticks.roundToInt()

        fun create(
            level: ServerLevel,
            center: Vec3,
            variant: FireGrenadeVariant,
            flameMap: FlameMap,
            lifetime: Int,
            debugMode: Boolean = false
        ): FireRegionEntity? {
            val entity = FireGrenadeRegistryHelper.fireRegionEntity.get().create(level)?:return null
            entity.variant = variant
            entity.flameMap = flameMap
            entity.setPos(center)
            entity.debugMode = debugMode
            entity.boundingBox = flameMap.boundingBox
            entity.lifetime = lifetime
            entity.hasInitialized = true

            if (level.addFreshEntity(entity)) {
                return entity
            } else {
                return null
            }
        }
    }

    override fun shouldBeSaved(): Boolean = false

    override fun defineSynchedData() {
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag) {
    }

    override fun onAddedToWorld() {
        super.onAddedToWorld()
        this.runOnServer {
            FireRegionEntity.serverTrackedEntities.add(this)
        }
        this.runOnClient {
            FireRegionEntity.clientTrackedEntities.add(this)
        }
    }

    override fun onRemovedFromWorld() {
        super.onRemovedFromWorld()
        this.runOnServer {
            FireRegionEntity.serverTrackedEntities.remove(this)
        }
        this.runOnClient {
            FireRegionEntity.clientTrackedEntities.remove(this)
        }
    }

    override fun makeBoundingBox(): AABB {
        if (hasInitialized) {
            return this.flameMap.boundingBox
        } else {
            return super.makeBoundingBox()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun writeSpawnData(buffer: FriendlyByteBuf) {
        require(this.hasInitialized)
        val data = SpawnData(this.variant, this.flameMap, this.debugMode, this.lifetime)
        buffer.writeByteArray(ProtoBuf.encodeToByteArray(SpawnData.serializer(), data))
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun readSpawnData(additionalData: FriendlyByteBuf) {
        val data = ProtoBuf.decodeFromByteArray(SpawnData.serializer(), additionalData.readByteArray())
        this.variant = data.variant
        this.flameMap = data.flameMap
        this.debugMode = data.debugMode
        this.boundingBox = flameMap.boundingBox
        this.lifetime = data.lifetime

        this.fireLoopSoundInstance =
            SimpleSoundInstance(
                this.variant.sounds().fireLoop.get().location,
                SoundSource.PLAYERS,
                1f,
                1f,
                this.random, true, 0, SoundInstance.Attenuation.LINEAR,
                this.x, this.y, this.z, false,
            )
        Minecraft.getInstance().soundManager.play(this.fireLoopSoundInstance)

        this.hasInitialized = true
    }

    override fun getAddEntityPacket(): Packet<ClientGamePacketListener> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun tick() {
        super.tick()
        this.runOnServer {
            if (this.tickCount > this.lifetime) {
                this.discard()
            }
            if (!this.hasInitialized) {
                return@runOnServer
            }
            if (this.debugMode) {
                return@runOnServer
            }
            this.damageTick()
        }
        this.runOnClient {
            if (!this.hasInitialized) {
                return@runOnClient
            }
            // Use equal sign for the sound to only play once
            if (this.lifetime - this.tickCount == FireRegionEntity.FIRE_FADEOUT_SOUND_LENGTH_TICK) {
                Minecraft.getInstance().soundManager.stop(this.fireLoopSoundInstance)
                val instance = SimpleSoundInstance(
                    this.variant.sounds().fireLoopFadeOut.get(),
                    SoundSource.PLAYERS,
                    1f,
                    1f,
                    this.random,
                    this.x,
                    this.y,
                    this.z,
                )
                Minecraft.getInstance().soundManager.play(instance)
            }
        }
    }

    private fun damageTick() {
        require(!this.level().isClientSide)
        require(this.hasInitialized)
        // Don't deal damage when debug mode is enabled
        if (this.debugMode) {
            return
        }
        val damageTransitionTime = this.variant.config().firegrenade.damageTransitionTime.get()
        val damage = this.variant.config().firegrenade.damage.get()
        val damageNonPlayer = this.variant.config().common.damageNonPlayer.get()

        val damageTypeHolder = this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
            .getHolderOrThrow(this.variant.damageTypes().fire)

        val entityInRange = mutableSetOf<Int>()
        this.level().getEntities(this, this.boundingBox).filter {
            (damageNonPlayer && it is LivingEntity) || it is Player
        }
            .filter { this.flameMap.boxes.any() { box -> box.contains(it.position()) } }.forEach {
                val entity = it as LivingEntity
                entityInRange.add(it.id)
                val enterTick = this.entityPreviousInRange.getOrPut(it.id) { this.tickCount }
                val damageAmount = min(
                    Mth.lerp(
                        GrenadeDuration.fromTick(this.tickCount.toDouble() - enterTick).seconds / damageTransitionTime,
                        0.0,
                        damage
                    ), damage
                )
                val damageSource = DamageSource(damageTypeHolder, entity, this, this.position())
                if (entity.invulnerableTime <= 0) {
                    entity.hurtCancelKnockback(damageSource, damageAmount, 10)
                }
            }
        this.entityPreviousInRange =
            this.entityPreviousInRange.filterKeys { entityInRange.contains(it) }.toMutableMap()
    }

    @Serializable
    class SpawnData(
        val variant: FireGrenadeVariant,
        val flameMap: FlameMap,
        val debugMode: Boolean,
        val lifetime: Int,
    )
}
