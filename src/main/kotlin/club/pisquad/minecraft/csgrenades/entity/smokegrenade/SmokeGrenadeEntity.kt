package club.pisquad.minecraft.csgrenades.entity.smokegrenade

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.config.*
import club.pisquad.minecraft.csgrenades.entity.*
import club.pisquad.minecraft.csgrenades.entity.firegrenade.*
import club.pisquad.minecraft.csgrenades.enums.*
import club.pisquad.minecraft.csgrenades.network.*
import club.pisquad.minecraft.csgrenades.network.data.*
import club.pisquad.minecraft.csgrenades.network.message.smokegrenade.*
import club.pisquad.minecraft.csgrenades.particle.*
import club.pisquad.minecraft.csgrenades.registry.*
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import java.time.Duration
import java.time.Instant

class SmokeGrenadeEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) :
    ActivateAfterLandingGrenadeEntity(
        pEntityType,
        pLevel,
        GrenadeType.FLASH_BANG,
        ModConfig.SmokeGrenade.FUSE_TIME_AFTER_LANDING.get().toTick().toInt(),
    ) {

    private var lastPos: Vec3i = Vec3i(0, 0, 0)
    private val particles = mutableMapOf<Vec3i, List<SmokeGrenadeParticle>>()
    private var explosionTime: Instant? = null
//    private val spreadBlocksCache: MutableList<@Serializable BlockPos> = mutableListOf()

    // For freezing rotation after explosion
    private var hasSavedFinalRotation = false

    private var finalXRot = 0f
    private var finalYRot = 0f
    private var finalZRot = 0f

    override fun getDefaultItem(): Item = ModItems.SMOKE_GRENADE_ITEM.get()

//    companion object {
//        val spreadBlocksAccessor: EntityDataAccessor<List<@Serializable BlockPos>> = SynchedEntityData.defineId(
//            SmokeGrenadeEntity::class.java,
//            ModSerializers.blockPosListEntityDataSerializer,
//        )
//    }
//
//    override fun defineSynchedData() {
//        super.defineSynchedData()
//        this.entityData.define(spreadBlocksAccessor, listOf())
//    }

    fun registerParticle(particle: SmokeGrenadeParticle) {
        val pos = particle.pos.toVec3i()
        if (this.particles.containsKey(pos)) {
            this.particles[pos] = this.particles[pos]!!.plus(particle)
        } else {
            this.particles[pos] = listOf(particle)
        }
    }

//    fun clearSmokeWithinRange(position: Vec3, range: Double, printHeader: Boolean) {
//        if (printHeader) {
//            println("CS-GRENADES DEBUG: Clearing smoke at pos: $position with range: $range. Checking ALL particles.")
//            val totalParticles = this.particles.values.sumOf { it.size }
//            println("CS-GRENADES DEBUG: Total particles in map: $totalParticles")
//            if (totalParticles == 0) {
//                println("CS-GRENADES DEBUG: Cleared 0 particles because the map is empty.")
//                return // Nothing to do
//            }
//        }
//
//        var clearedCount = 0
//        var checkedCount = 0 // To avoid spamming logs
//
//        // Iterate over all lists of particles in the map
//        this.particles.values.forEach { particleList ->
//            // Iterate over each particle in the list
//            particleList.forEach { particle ->
//                // New Debugging: Print first few particle positions
//                if (printHeader && checkedCount < 5) {
//                    val dist = particle.pos.distanceTo(position)
//                    println("CS-GRENADES DEBUG:   - Checking particle at ${particle.pos}, distance to bullet: $dist")
//                }
//                checkedCount++
//
//                // Directly check the distance between the bullet and the particle
//                if (particle.pos.distanceToSqr(position) < range * range) { // Use distanceToSqr for performance
//                    particle.opacityTime = getRegenerationTime(particle.pos.distanceTo(position), range)
//                    clearedCount++
//                }
//            }
//        }
//
//        if (clearedCount > 0) {
//            println("CS-GRENADES DEBUG: Cleared $clearedCount particles at interpolated position.")
//        }
//    }

//    private fun getRegenerationTime(distance: Double, radius: Double): Int = ModConfig.SmokeGrenade.TIME_BEFORE_REGENERATE.get().millToTick().toInt() + linearInterpolate(
//        ModConfig.SmokeGrenade.REGENERATION_TIME.get().millToTick().toDouble(),
//        0.0,
//        distance / radius,
//    ).toInt()

    override fun tick() {
        super.tick()
        if (this.entityData.get(isActivatedAccessor)) {
            // Forcefully freeze rotation and position
            if (this.level().isClientSide) {
                if (!hasSavedFinalRotation) {
                    // Save the final rotation the first tick it's exploded
                    finalXRot = this.customXRot
                    finalYRot = this.customYRot
                    finalZRot = this.customZRot
                    hasSavedFinalRotation = true
                }
                // On every subsequent tick, force the rotation back to the saved values
                this.customXRot = finalXRot
                this.customYRot = finalYRot
                this.customZRot = finalZRot
                this.customXRotO = finalXRot
                this.customYRotO = finalYRot
                this.customZRotO = finalZRot

//                this.clientRenderEffect()
//                this.disperseSmokeByProjectiles()
            } else {
                if (this.explosionTime != null && Duration.between(
                        this.explosionTime,
                        Instant.now(),
                    ) > Duration.ofMillis(
                        ModConfig.SmokeGrenade.SMOKE_LIFETIME.get().toLong(),
                    )
                ) {
                    this.kill()
                }
                extinguishNearbyFires()
            }
        }
    }

    override fun activate() {
        super.activate()
        val points = SmokeSpreadCalculator(this.level() as ServerLevel, this.center).getResult().toList().map { RoundedVec3.fromVec3(it) }
        ModPacketHandler.sendMessageToPlayer(this.level().dimension(), SmokeGrenadeActivatedMessage(points))
    }

//    private fun disperseSmokeByProjectiles() {
//        val spreadBlocks = this.getSpreadBlocks()
//        if (spreadBlocks.isEmpty()) {
//            return
//        }
//
//        // --- Vanilla Arrow Logic ---
//        // A large bounding box to catch any arrows that might be nearby. The swept BB check is more precise.
//        val searchBB = this.boundingBox.inflate(64.0)
//        val nearbyArrows = this.level().getEntitiesOfClass(
//            AbstractArrow::class.java,
//            searchBB,
//        ) { arrow -> arrow.deltaMovement.lengthSqr() > 0.01 } // Only consider moving arrows
//
//        val smokeRadius = ModConfig.SmokeGrenade.SMOKE_RADIUS.get().toDouble()
//        val smokeFallingHeight = ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get().toDouble()
//        val smokeCloudBoundingBox = AABB(this.blockPosition()).inflate(smokeRadius).expandTowards(0.0, -smokeFallingHeight, 0.0)
//
//        nearbyArrows.forEach { arrow ->
//            // Use a "swept" bounding box to detect fast-moving entities that pass through the cloud in a single tick.
//            val delta = arrow.deltaMovement
//            val currentBB = arrow.boundingBox
//            val oldBB = currentBB.move(-delta.x, -delta.y, -delta.z)
//            val sweptBB = currentBB.minmax(oldBB)
//
//            if (smokeCloudBoundingBox.intersects(sweptBB)) {
// //                println("CS-GRENADES DEBUG: Swept BB intersection SUCCESS for Arrow.")
//                // Interpolate position to prevent tunneling
//                val posNow = arrow.position()
//                val posOld = posNow.subtract(delta)
//                val steps = (delta.length() / 0.5).toInt().coerceAtLeast(1).coerceAtMost(30) // Check every 50cm, with a higher cap
//                for (i in 0..steps) {
//                    val interpolatedPos = posOld.lerp(posNow, i.toDouble() / steps)
//                    this.clearSmokeWithinRange(interpolatedPos, ModConfig.SmokeGrenade.ARROW_CLEAR_RANGE.get(), i == 0)
//                }
//            }
//        }
//
//        // --- TACZ BULLET COMPATIBILITY ---
//        if (this.level().isClientSide && ModList.get().isLoaded("tacz")) {
//            val clientLevel = this.level() as? ClientLevel ?: return
//            val allRenderEntities = clientLevel.entitiesForRendering()
//
//            if (allRenderEntities.none()) {
//                return
//            }
//
//            val smokeCenter = this.position()
//            val smokeRadius = ModConfig.SmokeGrenade.SMOKE_RADIUS.get().toDouble()
//            val smokeFallingHeight = ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get().toDouble()
//            val smokeCloudBoundingBox = AABB(BlockPos.containing(smokeCenter)).inflate(smokeRadius).expandTowards(0.0, -smokeFallingHeight, 0.0)
//
//            allRenderEntities.forEach { entity ->
//                // Use a "swept" bounding box to detect fast-moving entities that pass through the cloud in a single tick.
//                val delta = entity.deltaMovement
//                // If the entity hasn't moved, we don't need to check it.
//                if (delta.lengthSqr() == 0.0) return@forEach
//
//                val currentBB = entity.boundingBox
//                val oldBB = currentBB.move(-delta.x, -delta.y, -delta.z)
//                val sweptBB = currentBB.minmax(oldBB)
//
//                // Check if the swept path intersects the smoke cloud
//                if (smokeCloudBoundingBox.intersects(sweptBB)) {
//                    if (entity::class.java.name == "com.tacz.guns.entity.EntityKineticBullet") {
// //                        println("CS-GRENADES DEBUG: Swept BB intersection SUCCESS for Kinetic Bullet.")
//
//                        val posNow = entity.position()
//                        val finalClearRange = ModConfig.SmokeGrenade.BULLET_CLEAR_RANGE.get()
//                        val posOld = posNow.subtract(delta)
//
//                        // Interpolation logic remains the same
//                        val steps = (delta.length() / 0.5).toInt().coerceAtLeast(1).coerceAtMost(30)
//                        for (i in 0..steps) {
//                            val interpolatedPos = posOld.lerp(posNow, i.toDouble() / steps)
//                            this.clearSmokeWithinRange(interpolatedPos, finalClearRange, i == 0)
//                        }
//                    }
//                }
//            }
//        }
//    }

    override fun onAddedToWorld() {
        super.onAddedToWorld()
        lastPos = this.position().toVec3i()
    }

    override fun onHitBlock(result: BlockHitResult) {
        // If the smoke hit the ground with in any incendiary's range, it will emit right away
        super.onHitBlock(result)
        if (result.direction == Direction.UP) {
            if (this.extinguishNearbyFires() > 0) {
//                this.entityData.set(isLandedAccessor, true)
                if (this.level() is ServerLevel && result.isInside) {
                    this.setPos(Vec3(this.position().x, result.blockPos.y + 1.0, this.position().z))
                }
            }
        }
    }

    private fun extinguishNearbyFires(): Int {
        val extinguishedFires: List<AbstractFireGrenadeEntity>
        val smokeRadius = ModConfig.SmokeGrenade.SMOKE_RADIUS.get()
        val smokeFallingHeight = ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get()
        if (this.entityData.get(isActivatedAccessor)) {
            val bb = AABB(this.blockPosition()).inflate(
                smokeRadius.toDouble(),
                smokeFallingHeight.toDouble(),
                smokeRadius.toDouble(),
            )

            extinguishedFires = this.level().getEntitiesOfClass(
                AbstractFireGrenadeEntity::class.java,
                bb,
            ) {
                it.entityData.get(isActivatedAccessor) && canDistinguishFire(it.position())
            }
        } else {
            val bb = AABB(this.blockPosition()).inflate(ModConfig.FireGrenade.FIRE_RANGE.get().toDouble())
            extinguishedFires = this.level().getEntitiesOfClass(
                AbstractFireGrenadeEntity::class.java,
                bb,
            ) {
                it.entityData.get(isActivatedAccessor) && it.getSpreadBlocks()
                    .any { pos -> pos.above().center.distanceToSqr(this.position()) < 2 }
            }
        }

        if (this.level() is ServerLevel) {
            extinguishedFires.forEach {
                it.extinguish()
            }
        }
        return extinguishedFires.size
    }

    //    private fun clientRenderEffect() {
//        val player = Minecraft.getInstance().player ?: return
//        val distance = this.position().subtract(player.position()).length()
//
//        // Sounds
//        val soundManager = Minecraft.getInstance().soundManager
//        val soundEvent =
//            if (distance > 30) ModSoundEvents.SMOKE_EXPLODE_DISTANT.get() else ModSoundEvents.SMOKE_EMIT.get()
//        val soundType =
//            if (distance > 30) SoundTypes.SMOKE_GRENADE_EXPLODE_DISTANT else SoundTypes.SMOKE_GRENADE_EMIT
//
//        val soundInstance = EntityBoundSoundInstance(
//            soundEvent,
//            SoundSource.AMBIENT,
//            SoundUtils.getVolumeFromDistance(distance, soundType).toFloat(),
//            1f,
//            this,
//            0,
//        )
//        soundManager.play(soundInstance)
//
//        // Particles
//        SmokeRenderManager.render(
//            Minecraft.getInstance().particleEngine,
//            this.position(),
//            this,
//        )
//    }
    override fun getHitDamageSource(hitEntity: LivingEntity): DamageSource {
        val registryAccess = this.level().registryAccess()
        val damageTypeHolder = registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.SMOKEGRENADE_HIT)
        return if (hitEntity == this.owner) {
            DamageSource(damageTypeHolder, this)
        } else {
            DamageSource(damageTypeHolder, this, this.owner)
        }
    }

    //    fun canDistinguishFire(position: Vec3): Boolean = this.getSpreadBlocks().any { it.center.distanceToSqr(position) < 2.0 }
    fun canDistinguishFire(position: Vec3): Boolean = false
}
