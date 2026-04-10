package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

data class SmokeInteractionType(
    val canOccupy: Boolean,
    val spreadability: Spreadability
) {

    companion object {
        val AIR = SmokeInteractionType(
            true, Spreadability.ALL
        )
        val SOLID = SmokeInteractionType(
            false,
            Spreadability.NONE
        )
        val WATER = SmokeInteractionType(
            false,
            Spreadability.NONE
        )

        fun fromPosition(level: Level, position: BlockPos): SmokeInteractionType {
            return SmokeInteractionType(
                OccupationHelper.dispatch(level, position),
                Spreadability.dispatch(level, position)
            )
        }
    }
}

private object OccupationHelper {
    fun dispatch(level: Level, position: BlockPos): Boolean {
        val blockState = level.getBlockState(position)

        if (blockState.isAir) {
            return true
        }
        if (blockState.isCollisionShapeFullBlock(level, position)) {
            return false
        }

        return true
    }

//    data class Context(val level: Level, val position: BlockPos, val blockState: BlockState)
}