package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.minecraft.world.phys.Vec3
import kotlin.time.measureTimedValue

class VoxelWorker(entity: SmokeGrenadeEntity) {
    val center: Vec3 = entity.center
    val coroutineWorker: Deferred<RegionVoxelState>

    val floodFillWorker: FloodFillWorker

    init {
        val (state, duration) = measureTimedValue {
            RegionVoxelState.fromCenter(entity.level(), this.center)
        }
        ModLogger.debug(duration, "Generate region state")

        floodFillWorker = FloodFillWorker(center, state)

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

    fun blockingUntilComplete(): VoxelMap {
        val result = runBlocking { coroutineWorker.await() }
        return VoxelMap(this.center, result.mapValues { (_, value) -> value.toVoxel() })
    }
}
