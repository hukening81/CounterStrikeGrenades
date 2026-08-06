package club.pisquad.minecraft.csgrenades.utils

import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class ExtendableBlockState(
    val north: Boolean,
    val south: Boolean,
    val west: Boolean,
    val east: Boolean
) {
    companion object {
        fun isExtendableBlock(blockState: BlockState): Boolean {
            return BlockStateProperties.NORTH in blockState.values
                    && BlockStateProperties.SOUTH in blockState.values
                    && BlockStateProperties.WEST in blockState.values
                    && BlockStateProperties.EAST in blockState.values
        }

        fun fromExtendableBlock(blockState: BlockState): ExtendableBlockState {
            require(this.isExtendableBlock(blockState))
            val north = blockState.getValue(BlockStateProperties.NORTH)
            val south = blockState.getValue(BlockStateProperties.SOUTH)
            val west = blockState.getValue(BlockStateProperties.WEST)
            val east = blockState.getValue(BlockStateProperties.EAST)
            return ExtendableBlockState(north, south, west, east)
        }
    }

    fun section(direction: Direction): Boolean {
        require(direction.axis != Direction.Axis.Y)
        return when (direction) {
            Direction.DOWN -> TODO()
            Direction.UP -> TODO()
            Direction.NORTH -> {
                this.north
            }

            Direction.SOUTH -> {
                this.south
            }

            Direction.WEST -> {
                this.west
            }

            Direction.EAST -> {
                this.east
            }
        }
    }
}