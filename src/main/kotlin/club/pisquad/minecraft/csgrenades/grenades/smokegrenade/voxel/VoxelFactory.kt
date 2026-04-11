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

        // Any Fluid and waterlogged
        // Currently we do not distinguish between source block and others
        if (!blockState.fluidState.isEmpty) {
            return WaterVoxel(position)
        }

        if (blockState.`is`(BlockTags.DOORS)) {
            return createDoor(position, blockState)
        }

        if (blockState.`is`(BlockTags.TRAPDOORS)) {
            return createTrapdoors(position, blockState)
        }


        return createSolid(position)
    }

    fun createAir(position: BlockPos): AirVoxel {
        return AirVoxel(position)
    }

    fun createSolid(position: BlockPos): SolidVoxel {
        return SolidVoxel(position)
    }

    fun createDoor(position: BlockPos, blockState: BlockState): DoorVoxel {
        val opened = blockState.getValue(BlockStateProperties.OPEN)
        val facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val hinge = blockState.getValue(BlockStateProperties.DOOR_HINGE)
        return DoorVoxel(position, facing, hinge, opened)
    }

    fun createTrapdoors(position: BlockPos, blockState: BlockState): TrapdoorVoxel {
        val opened = blockState.getValue(BlockStateProperties.OPEN)
        val facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val half = blockState.getValue(BlockStateProperties.HALF)

        return TrapdoorVoxel(position, half, facing, opened)
    }
}