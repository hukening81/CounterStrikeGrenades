package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils.SmokeShapeHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3

class FloodFillWorker(
    val origin: Vec3,
    val voxels: RegionVoxelState
) {
    var cycleStart: List<BlockPos> = listOf()

    fun compute(): RegionVoxelState {

        cycleStart = doInitialSpread()

        // Basic Shape
        while (cycleStart.isNotEmpty()) {
            cycleStart = computeCurrentCycle()
        }

        // Fill space below
        // TODO

        return voxels.filterNonEmpty()
    }

    private fun doInitialSpread(): List<BlockPos> {
        val centerBlockPos = BlockPos.containing(origin)
        val centerVoxel = voxels[centerBlockPos] ?: return emptyList()

        return centerVoxel.asOrigin(origin).map {
            it.run {
                val target = centerBlockPos.relative(it)
                val voxel = voxels[target] ?: return@run null

                val needUpdate = voxel.updateNeighborIntensity(
                    this.opposite,
                    centerVoxel.getNeighborIntensity(this)
                )
                if (needUpdate) {
                    target
                } else {
                    null
                }
            }
        }.filterNotNull()
    }


    private fun computeCurrentCycle(): List<BlockPos> {
        val nextCycle: MutableList<BlockPos> = mutableListOf()

        for (ele in cycleStart) {
            voxels[ele]?.run {
                Direction.entries.forEach {
                    val intensity = this.getNeighborIntensity(it)
                    val target = ele.relative(it)

                    if (!SmokeShapeHelper.isInsideBaseShape(origin, target.center)) {
                        return@forEach
                    }

                    val voxel = voxels[target] ?: return@forEach

                    val needUpdate = voxel.updateNeighborIntensity(it.opposite, intensity)
                    if (needUpdate) {
                        nextCycle.add(target)
                    }
                }
            }
        }
        return nextCycle
    }
}