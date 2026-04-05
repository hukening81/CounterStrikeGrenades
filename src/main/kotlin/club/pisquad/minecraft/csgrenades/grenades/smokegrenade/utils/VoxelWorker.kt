package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeEntity
import club.pisquad.minecraft.csgrenades.minus
import kotlinx.coroutines.*
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import org.joml.Vector2d
import kotlin.math.pow
import kotlin.math.sqrt

class VoxelWorker(entity: SmokeGrenadeEntity) {
    val coroutineWorker: Deferred<Map<BlockPos, Int>> = ComputeScope.async {
        floodFillWorker.compute()
    }

    val floodFillWorker: FloodFillWorker

    init {
        val center = entity.center
        val snapshot = generateSnapshot(center)
        floodFillWorker = FloodFillWorker(center, snapshot)
    }


    companion object {
        val ComputeScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    }

    fun isDone(): Boolean {
        return coroutineWorker.isCompleted
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
    fun isInsideBaseShape(position: Vec3, center: Vec3, delta: Double = 1.0): Boolean {
        val relativePos = position.minus(center)

        val c = getHalfFocalDistance(delta)
        val a = ModConfig.smokegrenade.smokeWidth.get().times(delta)
        val axis = Vector2d(relativePos.x, relativePos.z).normalize()
        val f1 = axis.mul(c)
        val focus1 = Vec3(f1.x, 0.0, f1.y)
        val f2 = axis.mul(-c)
        val focus2 = Vec3(f2.x, 0.0, f2.y)

        return (relativePos.distanceTo(focus1) + relativePos.distanceTo(focus2)) < 2 * a
    }

    fun getHalfFocalDistance(delta: Double = 1.0): Double {
        val width = ModConfig.smokegrenade.smokeWidth.get().times(delta)
        val height = ModConfig.smokegrenade.smokeHeight.get().times(delta)
        return sqrt(width.pow(2) - height.pow(2))
    }

    fun getAllPossibleBlocks(center: Vec3): List<BlockPos> {
        TODO()
    }
}

private fun generateSnapshot(center: Vec3): RegionSnapShot {

    TODO()
}