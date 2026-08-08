package club.pisquad.minecraft.csgrenades.grenades.hegrenade

import club.pisquad.minecraft.csgrenades.distanceToLine
import club.pisquad.minecraft.csgrenades.epsilon
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeRegionEntity
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages.SmokePatch
import club.pisquad.minecraft.csgrenades.hurtCancelKnockback
import club.pisquad.minecraft.csgrenades.points
import club.pisquad.minecraft.csgrenades.runOnServer
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import kotlin.math.max

object HEGrenadeHelper {
    fun dealDamage(grenade: HEGrenadeEntity) {
        grenade.runOnServer {
            val location = grenade.center
            val level = grenade.level() as ServerLevel
            val grenadeType = grenade.grenadeType
            val range = HEGrenadeConfig.explosion.radius.get()

            val entities = level.getEntitiesOfClass(
                LivingEntity::class.java, AABB.ofSize(
                    center, range, range, range
                )
            )

            val key = HEGrenadeDamageTypes.explosion
            val holder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key)


            for (e in entities) {
                val damage = if (e is Player) {
                    calculatePlayerDamage(level, location, e)
                } else {
                    calculateDamage(level, location, e.boundingBox)
                }
                if (damage < Double.epsilon()) {
                    continue
                }
                val source = DamageSource(holder, grenade, e, location)
                e.hurtCancelKnockback(source, damage)
            }
        }
    }

    fun calculateDamage(level: Level, position: Vec3, aabb: AABB): Double {
        val center = aabb.center
        val points = aabb.points().sortedBy { it.distanceToLine(position, center) }

        var nonBlocking = false
        repeat(4) {
            val context = ClipContext(position, center, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)
            val result = level.clip(context)
            if (result.type == HitResult.Type.MISS) {
                nonBlocking = true
                return@repeat
            }
        }
        if (!nonBlocking) {
            return 0.0
        }
        val distance = position.distanceTo(center)
        val damageAmount = HEGrenadeConfig.explosion.damageAmount.get()
        return Mth.lerp(distance / HEGrenadeConfig.explosion.radius.get(), damageAmount, 0.0)
            .coerceIn(0.0, damageAmount)
    }

    fun calculatePlayerDamage(level: Level, position: Vec3, player: Player): Double {
        val headRange = 1.5
        var headDamage: Double = 0.0
        val headAABB = AABB.ofSize(
            player.eyePosition,
            0.25,
            0.25,
            0.25,
        )
        if (headAABB.center.distanceTo(position) < headRange) {
            headDamage =
                calculateDamage(level, position, headAABB) * HEGrenadeConfig.explosion.headDamageMultiplier.get()
        }

        val dimension = player.getDimensions(player.pose)
        val bodyAABB = AABB.ofSize(
            player.position().add(0.0, dimension.height / 2.0, 0.0),
            dimension.width / 2.0,
            dimension.height / 2.0,
            dimension.width / 2.0
        )
        val bodyDamage = calculateDamage(level, position, bodyAABB)
        return max(headDamage, bodyDamage)
    }

    fun blowUpNearbySmokeGrenade(grenade: HEGrenadeEntity) {
        val radius = HEGrenadeConfig.explosion.smokeClearRadius.get()
        val center = grenade.center
        val patch = SmokePatch.Explosion(center, radius)
        val bb = AABB.ofSize(center, radius, radius, radius)
        SmokeRegionEntity.serverTrackedRegions.get(grenade.level().dimension())?.forEach {
            if (bb.intersects(it.boundingBox)) {
                it.applyPatch(patch)
            }
        }
    }
}
