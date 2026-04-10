package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

object VoxelFactory {
    fun create(level: Level, position: BlockPos): VoxelState {
        val blockState = level.getBlockState(position)
        if (blockState.isAir) {
            return createAir(position)
        }
        return createSolid(position)
    }

    fun createAir(position: BlockPos): AirVoxel {
        return AirVoxel(position)
    }

    fun createSolid(position: BlockPos): SolidVoxel {
        return SolidVoxel(position)
    }
}