package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.minus
import club.pisquad.minecraft.csgrenades.toVec3
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3

class FloodFillWorker(
    val center: Vec3,
    val snapshot: RegionSnapShot
) {
    val state: MutableMap<BlockPos, Int> = mutableMapOf(
        BlockPos.containing(center) to ModConfig.smokegrenade.initialQuantity.get()
    )

    var cycleStart: List<BlockPos> = listOf()

    fun compute(): Map<BlockPos, Int> {
        if (!snapshot[BlockPos.containing(center)]!!.canOccupy) {
            return emptyMap()
        }
        // Use this to accurately handle situations like the smoke is on one side of wall and inside the wall
        doInitialSpread()

        // Basic Shape
        while (cycleStart.isNotEmpty()) {
            cycleStart = computeCurrentCycle()
        }

        // Fill space below
        // TODO

        return state
    }

    private fun doInitialSpread() {
        val centerBlockPos = BlockPos.containing(center)
        val offset = center.minus(BlockPos.containing(center).toVec3())

        val interactionType = snapshot[centerBlockPos] ?: return

        val newCycleStart: MutableList<BlockPos> = mutableListOf()

        val surroundingQuantity = state[centerBlockPos]!! - 1

        val addSurrounding = { direciton: Direction ->
            val newPos = centerBlockPos.relative(direciton)
            newCycleStart.add(newPos)
            state[newPos] = surroundingQuantity
        }
        val addAxis = { inverse: Boolean, primary: Direction ->
            if (inverse) {
                addSurrounding(primary.opposite)
                if (interactionType.spreadability.canSpread(primary.opposite)) {
                    addSurrounding(primary.opposite)
                }
            } else {
                addSurrounding(primary.opposite)
                if (interactionType.spreadability.canSpread(primary)) {
                    addSurrounding(primary)
                }
            }
        }
        addAxis(offset.x > 0.5, Direction.EAST)
        addAxis(offset.y > 0.5, Direction.UP)
        addAxis(offset.z > 0.5, Direction.NORTH)

        cycleStart = newCycleStart
    }


    private fun computeCurrentCycle(): List<BlockPos> {
        val nextCycle: MutableList<BlockPos> = mutableListOf()

        val trySpread = marker@{
                center: BlockPos,
                direction: Direction,
            ->
            val newPos = center.relative(direction)
            if (!SmokeShapeHelper.isInsideBaseShape(this.center, newPos.center)) {
                return@marker
            }

            val newQuantity = state[center]!! - 1

            snapshot[newPos]?.run {
                if (this.canOccupy) {
                    if ((state[newPos] ?: 0) < newQuantity) {
                        state[newPos] = newQuantity
                        nextCycle.add(newPos)
                    }
                }
            }
        }

        for (ele in cycleStart) {
            snapshot[ele]?.run {
                Direction.entries.filter { this.spreadability.canSpread(it) }.forEach {
                    trySpread(ele, it)
                }
            }
        }
        return nextCycle
    }
}
