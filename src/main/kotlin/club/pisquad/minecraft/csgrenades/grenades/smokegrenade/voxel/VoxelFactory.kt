package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties

object VoxelFactory {
    fun create(level: Level, position: BlockPos): VoxelState {
        val blockState = level.getBlockState(position)
        if (blockState.isAir) {
            return createAir(position)
        }
        if (blockState.`is`(BlockTags.DOORS)) {
            return createDoor(level, position, blockState)
        }
        return createSolid(position)
    }

    fun createAir(position: BlockPos): AirVoxel {
        return AirVoxel(position)
    }

    fun createSolid(position: BlockPos): SolidVoxel {
        return SolidVoxel(position)
    }

    fun createDoor(level: Level, position: BlockPos, blockState: BlockState): DoorVoxel {
        val opened = blockState.getValue(BlockStateProperties.OPEN)
        val facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val hinge = blockState.getValue(BlockStateProperties.DOOR_HINGE)
        return DoorVoxel(position, facing, hinge, opened)
    }
}