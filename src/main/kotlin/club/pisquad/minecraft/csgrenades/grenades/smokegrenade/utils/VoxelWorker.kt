package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeEntity
import club.pisquad.minecraft.csgrenades.minus
import kotlinx.coroutines.*
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.measureTimedValue

class VoxelWorker(entity: SmokeGrenadeEntity) {
    val coroutineWorker: Deferred<Map<BlockPos, Int>>

    val floodFillWorker: FloodFillWorker

    init {
        val center = entity.center
        val (snapshot, duration) = measureTimedValue {
            RegionSnapShot.fromCenter(center, entity.level())
        }
        ModLogger.debug(duration, "Generate region snapshot")

        floodFillWorker = FloodFillWorker(center, snapshot)
        coroutineWorker = ComputeScope.async {
            val (result, duration) = measureTimedValue {
                floodFillWorker.compute()
            }
            ModLogger.debug(duration, "Compute smoke spread")
            result
        }
    }


    companion object {
        val ComputeScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getResultOrNull(): Map<BlockPos, Int>? {
        return if (coroutineWorker.isCompleted) {
            coroutineWorker.getCompleted()
        } else {
            null
        }
    }

    fun blockingUntilComplete(): Map<BlockPos, Int> {
        return runBlocking { coroutineWorker.await() }
    }
}

object SmokeShapeHelper {
    fun isInsideBaseShape(center: Vec3, position: Vec3, delta: Double = 1.0): Boolean {
        val relativePos = position.minus(center)

        val c = getHalfFocalDistance(delta)
        val a = ModConfig.smokegrenade.smokeWidth.get().times(delta)
        val axis = Vec2(relativePos.x.toFloat(), relativePos.z.toFloat()).normalized()
        val f1 = axis.scale(c.toFloat())
        val focus1 = Vec3(f1.x.toDouble(), 0.0, f1.y.toDouble())
        val f2 = axis.scale(-c.toFloat())
        val focus2 = Vec3(f2.x.toDouble(), 0.0, f2.y.toDouble())

        return (relativePos.distanceTo(focus1) + relativePos.distanceTo(focus2)) < 2 * a
    }

    fun getHalfFocalDistance(delta: Double = 1.0): Double {
        val width = ModConfig.smokegrenade.smokeWidth.get().times(delta)
        val height = ModConfig.smokegrenade.smokeHeight.get().times(delta)
        return sqrt(width.pow(2) - height.pow(2))
    }

    fun getAllPossibleBlocks(center: Vec3): List<BlockPos> {
        val width = ModConfig.smokegrenade.smokeWidth.get()
        val height = ModConfig.smokegrenade.smokeHeight.get()
        val maxFall = ModConfig.smokegrenade.maxFall.get()

        return buildList {
            for (x in floor(center.x - width).toInt()..ceil(center.x + width).toInt()) {
                for (z in floor(center.z - width).toInt()..ceil(center.z + width).toInt()) {
                    for (y in floor(center.y - height - maxFall).toInt()..ceil(center.y + height).toInt()) {
                        add(BlockPos(x, y, z))
                    }
                }
            }
        }
    }
}