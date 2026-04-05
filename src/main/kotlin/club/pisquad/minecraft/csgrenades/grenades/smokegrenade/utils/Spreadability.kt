package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

class Spreadability(vararg val directions: Direction) {
    companion object {
        val ALL: Spreadability = Spreadability(
            Direction.UP,
            Direction.DOWN,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST,
        )

        val NONE: Spreadability = Spreadability()

        fun dispatch(level: Level, center: BlockPos): Spreadability {
            return Spreadability.ALL
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
}

class RegionSnapShot : MutableMap<BlockPos, SmokeInteractionType> by HashMap() {
    companion object {
        fun fromCenter(center: Vec3, level: Level): RegionSnapShot {
            val result = RegionSnapShot()
            SmokeShapeHelper.getAllPossibleBlocks(center).forEach {
                result[it] = SmokeInteractionType.fromPosition(level, it)
            }
            return result
        }
    }

}
