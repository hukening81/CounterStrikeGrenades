package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.config.ModConfig
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

fun <T : Entity> T.runOnServer(task: T.() -> Unit) {
    if (!this.level().isClientSide) {
        task(this)
    }
}

fun <T : Entity> T.runOnClient(task: T.() -> Unit) {
    if (this.level().isClientSide) {
        task(this)
    }
}
fun Vec3.toVec3i(): Vec3i = Vec3i(x.toInt(), y.toInt(), z.toInt())

fun Vec3.snapToAxis(): Direction {
    val absX = abs(x)
    val absY = abs(y)
    val absZ = abs(z)
    return when {
        absX >= absY && absX >= absZ -> if (x > 0) Direction.NORTH else Direction.SOUTH
        absY >= absX && absY >= absZ -> if (y > 0) Direction.UP else Direction.DOWN
        else -> if (z > 0) Direction.EAST else Direction.WEST
    }
}

fun BlockPos.toVec3(): Vec3 = Vec3(x.toDouble(), y.toDouble(), z.toDouble())

fun Vec3i.toVec3(): Vec3 = Vec3(x.toDouble(), y.toDouble(), z.toDouble())

fun getTimeFromTickCount(tickCount: Double): Double = tickCount / 20.0

fun getRandomLocationFromSphere(center: Vec3, radius: Double): Vec3 {
    while (true) {
        val posDelta = Vec3(
            Random.nextDouble() * radius * 2 - radius,
            Random.nextDouble() * radius * 2 - radius,
            Random.nextDouble() * radius * 2 - radius,
        )
        if (posDelta.length() < radius) {
            return center.add(posDelta)
        }
    }
}

fun getRandomLocationFromCircle(center: Vec2, radius: Double): Vec2 {
    while (true) {
        val posDelta = Vec2(
            (Random.nextDouble() * radius * 2 - radius).toFloat(),
            (Random.nextDouble() * radius * 2 - radius).toFloat(),
        )
        if (posDelta.length() < radius) {
            return center.add(posDelta)
        }
    }
}

fun getRandomLocationFromBlockSurface(position: BlockPos): Vec3 {
    val x = Random.nextDouble()
    val z = Random.nextDouble()
    return Vec3(position.x + x, position.y + 1.0, position.z + z)
}

fun isPositionInSmoke(level: Level, pos: Vec3): Boolean {
    return false
//    val blockPos = BlockPos.containing(pos)
//    return level.getEntitiesOfClass(
//        SmokeGrenadeEntity::class.java,
//        AABB(BlockPos(pos.toVec3i())).inflate(
//            ModConfig.SmokeGrenade.SMOKE_RADIUS.get() * 2.0,
//            ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get() + ModConfig.SmokeGrenade.SMOKE_RADIUS.get() * 2.0,
//            ModConfig.SmokeGrenade.SMOKE_RADIUS.get() * 2.0,
//        ),
//    ).any {
//        it.getSpreadBlocks().any { block -> block == blockPos }
//    }
}

fun getBlockPosAround2D(pos: Vec3, radius: Int): List<BlockPos> {
    val posVec3 = BlockPos.containing(pos)
    val begin = posVec3.offset(-radius, 0, -radius)
    val result = mutableListOf<BlockPos>()
    repeat((radius * 2) + 1) { xOffset ->
        repeat((radius * 2) + 1) { zOffset ->
            result.add(BlockPos(begin.offset(xOffset, 0, zOffset)))
        }
    }
    return result
}

fun getBlocksAround3D(pos: Vec3, xRange: Int, yRange: Int, zRange: Int): List<BlockPos> {
    val posVec3 = BlockPos.containing(pos)
    val begin = posVec3.offset(-xRange, -yRange, -zRange)
    val result = mutableListOf<BlockPos>()
    repeat((xRange * 2) + 1) { xOffset ->
        repeat((yRange * 2) + 1) { yOffset ->
            repeat((zRange * 2) + 1) { zOffset ->
                result.add(
                    BlockPos(
                        begin.offset(
                            xOffset,
                            yOffset,
                            zOffset,
                        ),
                    ),
                )
            }
        }
    }
    return result
}

fun linearInterpolate(from: Double, to: Double, t: Double): Double = from + (to - from) * t

fun Long.millToTick(): Long = this.div(50)

fun Vec3.horizontalDistanceTo(other: Vec3): Double = sqrt(
    Vec2(this.x.toFloat(), this.z.toFloat()).distanceToSqr(Vec2(other.x.toFloat(), other.z.toFloat())).toDouble(),
)

fun BlockPos.horizontalDistanceToSqr(other: BlockPos): Double =
    Vec2(this.x.toFloat(), this.z.toFloat()).distanceToSqr(Vec2(other.x.toFloat(), other.z.toFloat())).toDouble()

fun BlockPos.horizontalDistanceTo(other: BlockPos): Double = sqrt(
    this.horizontalDistanceToSqr(other),
)

fun Double.toTick(): Long = this.times(20).toLong()

fun Double.toMetersPerTick(): Double {
    return this.div(20)
}

fun Double.toMetersPerSecond(): Double {
    return this.times(20)
}

fun Long.nanoSecondToSecond(): Double {
    return this.div(1_000_000_000.0)
}

fun Vec3.toMetersPerTick(): Vec3 {
    return Vec3(this.x.toMetersPerTick(), this.y.toMetersPerTick(), this.z.toMetersPerTick())
}

fun Vec3.toMetersPerSecond(): Vec3 {
    return Vec3(this.x.toMetersPerSecond(), this.y.toMetersPerSecond(), this.z.toMetersPerSecond())
}

fun renderTestParticleAtPosition(level: Level, position: Vec3) {
    level.addParticle(
        ParticleTypes.ASH,
        position.x,
        position.y,
        position.z,
        0.0,
        0.0,
        0.0,
    )
}

fun Vec3.minusGrenadeSizeOffset(): Vec3 {
    return this.minus(
        ModSettings.Entity.GRENADE_ENTITY_SIZE_HALF,
        ModSettings.Entity.GRENADE_ENTITY_SIZE_HALF,
        ModSettings.Entity.GRENADE_ENTITY_SIZE_HALF
    )
}

fun Vec3.addGrenadeSizeOffset(): Vec3 {
    return this.add(
        ModSettings.Entity.GRENADE_ENTITY_SIZE_HALF,
        ModSettings.Entity.GRENADE_ENTITY_SIZE_HALF,
        ModSettings.Entity.GRENADE_ENTITY_SIZE_HALF
    )
}

fun Double.isBetween(value1: Double, value2: Double): Boolean {
    return (value1 <= value2 && this >= value1 && this <= value2) || (value1 >= value2 && this <= value1 && this >= value2)
}

fun Player.getShootOrigin(): Vec3 {
    return this.eyePosition
}

fun Player.getEarPosition(): Vec3 {
    return this.position()
}

fun ServerLevel.getPlayersWithinMessageRange(center: Vec3): List<Player> {
    val range = ModConfig.messageRange.get()
    val box = AABB.ofSize(center, range, range, range)
    return this.getEntitiesOfClass(Player::class.java, box).toList()
}

internal fun Boolean.toInt(): Int {
    return if (this) {
        1
    } else {
        0
    }
}

fun horizontalDirections(): Set<Direction> {
    return Direction.entries.filter { it.axis.isHorizontal }.toSet()
}

internal fun Double.Companion.epsilon(): Double = 1.0E-7

internal fun Vec3.inverseAxis(axis: Direction.Axis): Vec3 {
    return when (axis) {
        Direction.Axis.X -> {
            Vec3(-this.x, this.y, this.z)
        }

        Direction.Axis.Y -> {
            Vec3(this.x, -this.y, this.z)
        }

        Direction.Axis.Z -> {
            Vec3(this.x, this.y, -this.z)
        }
    }
}

internal fun LivingEntity.hurtCancelKnockback(
    source: DamageSource,
    amount: Double,
    invulnerableTick: Int? = null
): Boolean {
    val originalKnockBackResistance =
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)?.baseValue?:0.0
    this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)?.baseValue = 1.0

    val movement = this.deltaMovement

    val result = this.hurt(source, amount.toFloat())
    if (invulnerableTick != null) {
        this.invulnerableTime = invulnerableTick
    }

    this.deltaMovement = movement
    this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)?.baseValue = originalKnockBackResistance

    return result
}

internal fun AABB.points(): List<Vec3> {
    val aabb = this
    return buildList {
        add(Vec3(aabb.minX, aabb.minY, aabb.minX))
        add(Vec3(aabb.minX, aabb.minY, aabb.maxX))
        add(Vec3(aabb.minX, aabb.maxY, aabb.minX))
        add(Vec3(aabb.minX, aabb.maxY, aabb.maxX))
        add(Vec3(aabb.maxX, aabb.minY, aabb.minX))
        add(Vec3(aabb.maxX, aabb.minY, aabb.maxX))
        add(Vec3(aabb.maxX, aabb.maxY, aabb.minX))
        add(Vec3(aabb.maxX, aabb.maxY, aabb.maxX))
    }
}