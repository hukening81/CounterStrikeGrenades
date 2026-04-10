package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

class Spreadability(vararg val axes: Direction.Axis) {
    companion object {
        val ALL: Spreadability = Spreadability(
            Direction.Axis.X, Direction.Axis.Y, Direction.Axis.Z
        )

        val NONE: Spreadability = Spreadability()

        fun dispatch(level: Level, center: BlockPos): Spreadability {
            val blockState = level.getBlockState(center)
            if (blockState.isAir) {
                return ALL
            }

            return NONE
        }


        fun trapdoor(context: Context): Spreadability {
            TODO()
        }

        fun door(context: Context): Spreadability {
            TODO()
        }

        fun glassPane(context: Context): Spreadability {
            TODO()
        }

        data class Context(
            val level: Level,
            val blockPos: BlockPos,
            val blockState: BlockState = level.getBlockState(blockPos),
        )
    }

    fun canSpread(axis: Direction.Axis): Boolean {
        return axis in axes
    }

    fun canSpread(direction: Direction): Boolean {
        return direction.axis in axes
    }
}

class RegionSnapShot : MutableMap<BlockPos, SmokeInteractionType> by HashMap() {
    companion object {
        fun fromCenter(center: Vec3, level: Level): RegionSnapShot {
            val startTime = System.currentTimeMillis()
            val result = RegionSnapShot()
            SmokeShapeHelper.getAllPossibleBlocks(center).forEach {
                result[it] = SmokeInteractionType.fromPosition(level, it)
            }
            return result
        }
    }

}
