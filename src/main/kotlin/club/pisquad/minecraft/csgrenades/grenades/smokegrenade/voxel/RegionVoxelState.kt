package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils.SmokeShapeHelper
import club.pisquad.minecraft.csgrenades.network.serializer.BlockPosSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

@Serializable
class RegionVoxelState(
    @Serializable(with = Vec3Serializer::class) val center: Vec3,
    private val voxels: MutableMap<@Serializable(with = BlockPosSerializer::class) BlockPos, VoxelState>
) : MutableMap<BlockPos, VoxelState> by voxels {

    companion object {
        fun fromCenter(level: Level, center: Vec3): RegionVoxelState {
            val voxels = mutableMapOf<BlockPos, VoxelState>()

            val blocks = SmokeShapeHelper.getAllPossibleBlocks(center)
            blocks.forEach {
                voxels[it] = VoxelFactory.create(level, it)
            }

            return RegionVoxelState(center, voxels)
        }
    }

    fun filterNonEmpty(): RegionVoxelState {
        return RegionVoxelState(
            this.center,
            this.filter { (_, state) -> state.isOccupied() }.toMutableMap()
        )
    }
}