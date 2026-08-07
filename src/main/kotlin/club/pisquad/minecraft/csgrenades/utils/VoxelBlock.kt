package club.pisquad.minecraft.csgrenades.utils

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.ComputeVoxel
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelPos
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.AbstractChestBlock
import net.minecraft.world.level.block.BedBlock
import net.minecraft.world.level.block.CrossCollisionBlock
import net.minecraft.world.level.block.DirtPathBlock
import net.minecraft.world.level.block.FenceGateBlock
import net.minecraft.world.level.block.SnowLayerBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.ChestType
import net.minecraft.world.level.block.state.properties.DoorHingeSide
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.level.block.state.properties.SlabType
import net.minecraft.world.level.block.state.properties.StairsShape
import java.util.*

sealed interface VoxelBlock {
    fun check(context: VoxelBlockContext): Boolean
    fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel>
}

object VoxelBlockDelegator {
    val candidates: List<VoxelBlock> = buildList {
        add(AirVoxelBlock)
        add(SmallHitBoxVoxelBlock)
        add(SolidVoxelBlock)
        add(SlabVoxelBlock)
        add(StairVoxelBlock)
        add(CrossCollisionVoxelBlock)
        add(DoorVoxelBlock)
        add(TrapdoorVoxelBlock)
        add(SignVoxelBlock)
        add(BedVoxelBlock)
        add(FenceGateVoxelBlock)
        add(ChestVoxelBlock)
        add(SnowLayersVoxelBlock)
        add(DirtPathVoxelBlock)
    }

    fun delegate(context: VoxelBlockContext): VoxelBlock {
        val result = candidates.find {
            it.check(context)
        } ?: SolidVoxelBlock

        ModLogger.trace("VoxelBlockDelegator: block [{}] delegated to [{}]", context.blockState.block, result)

        return result
    }
}

data class VoxelBlockContext(
    val blockPos: BlockPos,
    val blockState: BlockState,
    val level: Level
)

object AirVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.isAir
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        return buildMap {
            Quadrant.entries.forEach {
                put(
                    it, ComputeVoxel.create(
                        context.blockPos, it,
                        GridConnectivity.ALL,
                        false
                    )
                )
            }
        }
    }
}

object SolidVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.isCollisionShapeFullBlock(context.level, context.blockPos)
                || !context.blockState.fluidState.isEmpty // Also check for waterlogged blocks
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        return buildMap {
            Quadrant.entries.forEach {
                put(
                    it, ComputeVoxel.create(
                        context.blockPos, it,
                        GridConnectivity.NONE,
                    )
                )
            }
        }
    }
}

object SmallHitBoxVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.getCollisionShape(context.level, context.blockPos).isEmpty
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        return AirVoxelBlock.voxels(context)
    }
}

object DoorVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.`is`(BlockTags.DOORS)
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val opened = context.blockState.getValue(BlockStateProperties.OPEN)
        val facing = context.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val hinge = context.blockState.getValue(BlockStateProperties.DOOR_HINGE)

        val blockingSide = if (opened) {
            if (hinge == DoorHingeSide.LEFT) {
                facing.opposite.clockWise
            } else {
                facing.opposite.counterClockWise
            }
        } else {
            facing.opposite
        }

        return buildMap {
            Quadrant.entries.forEach {
                val connectivity = if (it.x == blockingSide || it.z == blockingSide) {
                    GridConnectivity.exclude(blockingSide)
                } else {
                    GridConnectivity.ALL
                }
                put(
                    it, ComputeVoxel.create(
                        context.blockPos, it,
                        connectivity,
                    )
                )
            }
        }
    }
}

object TrapdoorVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.`is`(BlockTags.TRAPDOORS)
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val opened = context.blockState.getValue(BlockStateProperties.OPEN)
        val facing = context.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val half = context.blockState.getValue(BlockStateProperties.HALF)

        val blockingSide = if (opened) {
            facing.opposite
        } else {
            if (half == Half.TOP) {
                Direction.UP
            } else {
                Direction.DOWN
            }
        }
        return buildMap {
            Quadrant.entries.forEach {
                val connectivity = if (it.x == blockingSide || it.y == blockingSide || it.z == blockingSide) {
                    GridConnectivity.exclude(blockingSide)
                } else {
                    GridConnectivity.ALL
                }
                put(
                    it, ComputeVoxel.create(
                        context.blockPos, it,
                        connectivity,
                    )
                )
            }
        }
    }
}

object SignVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.`is`(BlockTags.ALL_HANGING_SIGNS) || context.blockState.`is`(BlockTags.SIGNS)
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        return buildMap {
            Quadrant.entries.forEach {
                put(
                    it, ComputeVoxel.create(
                        context.blockPos, it,
                        GridConnectivity.ALL
                    )
                )
            }
        }
    }
}

object SlabVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.`is`(BlockTags.SLABS)
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val type = context.blockState.getValue(BlockStateProperties.SLAB_TYPE)
        val directions = when (type) {
            SlabType.TOP -> listOf(Direction.UP)
            SlabType.BOTTOM -> listOf(Direction.DOWN)
            SlabType.DOUBLE -> listOf(Direction.UP, Direction.DOWN)
        }
        return buildMap {
            Quadrant.entries.forEach {
                val connectivity = if (directions.contains(it.y)) {
                    GridConnectivity.NONE
                } else {
                    GridConnectivity.ALL
                }
                put(
                    it, ComputeVoxel(
                        VoxelPos.fromBlockAndQuadrant(context.blockPos, it),
                        connectivity,
                    )
                )
            }
        }
    }
}

object CrossCollisionVoxelBlock : VoxelBlock {

    // Iron bars
    // Fences
    // Glass panes
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.block is CrossCollisionBlock
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val north = context.blockState.getValue(BlockStateProperties.NORTH)
        val south = context.blockState.getValue(BlockStateProperties.SOUTH)
        val west = context.blockState.getValue(BlockStateProperties.WEST)
        val east = context.blockState.getValue(BlockStateProperties.EAST)

        val quadrantConnectivity: MutableMap<Quadrant, EnumSet<Direction>> = buildMap {
            Quadrant.entries.forEach {
                put(it, EnumSet.allOf(Direction::class.java))
            }
        }.toMutableMap()

        if (north) {
            quadrantConnectivity.forEach { (quadrant, directions) ->
                if (quadrant.z == Direction.NORTH) {
                    directions.remove(quadrant.x.opposite)
                }
            }
        }
        if (south) {
            quadrantConnectivity.forEach { (quadrant, directions) ->
                if (quadrant.z == Direction.SOUTH) {
                    directions.remove(quadrant.x.opposite)
                }
            }
        }
        if (west) {
            quadrantConnectivity.forEach { (quadrant, directions) ->
                if (quadrant.x == Direction.WEST) {
                    directions.remove(quadrant.z.opposite)
                }
            }
        }
        if (east) {
            quadrantConnectivity.forEach { (quadrant, directions) ->
                if (quadrant.x == Direction.EAST) {
                    directions.remove(quadrant.z.opposite)
                }
            }
        }
        return buildMap {
            quadrantConnectivity.forEach { (quadrant, directions) ->
                put(
                    quadrant, ComputeVoxel.create(
                        context.blockPos, quadrant,
                        GridConnectivity.from(*directions.toTypedArray()),
                    )
                )
            }
        }
    }
}

object StairVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.block is StairBlock
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val facing = context.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val shape = context.blockState.getValue(BlockStateProperties.STAIRS_SHAPE)
        val half = context.blockState.getValue(BlockStateProperties.HALF)

        val left = facing.counterClockWise
        val right = facing.clockWise

        val solidParts = mutableSetOf<Quadrant>()

        when (half) {
            Half.TOP -> {
                solidParts.addAll(Quadrant.Regions.UP)
            }

            Half.BOTTOM -> {
                solidParts.addAll(Quadrant.Regions.DOWN)
            }
        }

        when (shape) {
            StairsShape.STRAIGHT -> {
                solidParts.addAll(Quadrant.Regions.fromDirection(facing))
            }

            StairsShape.INNER_LEFT -> {
                solidParts.addAll(Quadrant.Regions.fromDirection(facing))
                solidParts.addAll(Quadrant.Regions.fromDirection(left))
            }

            StairsShape.INNER_RIGHT -> {
                solidParts.addAll(Quadrant.Regions.fromDirection(facing))
                solidParts.addAll(Quadrant.Regions.fromDirection(right))
            }

            StairsShape.OUTER_LEFT -> {
                solidParts.addAll(Quadrant.Regions.fromDirection(facing, left))
            }

            StairsShape.OUTER_RIGHT -> {
                solidParts.addAll(Quadrant.Regions.fromDirection(facing, right))
            }
        }

        return buildMap {
            Quadrant.entries.forEach {
                val connectivity = if (solidParts.contains(it)) {
                    GridConnectivity.NONE
                } else {
                    GridConnectivity.ALL
                }
                put(it, ComputeVoxel.create(context.blockPos, it, connectivity))
            }
        }
    }
}

object BedVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.block is BedBlock
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        return buildMap {
            Quadrant.entries.forEach {
                put(
                    it, ComputeVoxel.create(
                        context.blockPos, it,
                        GridConnectivity.exclude(it.y.opposite),
                    )
                )
            }
        }
    }
}

object FenceGateVoxelBlock : VoxelBlock {
    // Fence is handled by CrossCollisionVoxelBlock
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.block is FenceGateBlock
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val opened = context.blockState.getValue(BlockStateProperties.OPEN)
        val facing = context.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

        val axis = facing.axis

        val getConnectivity = { quadrant: Quadrant ->
            if (axis == Direction.Axis.X) {
                GridConnectivity.exclude(quadrant.x.opposite)
            } else {
                GridConnectivity.exclude(quadrant.z.opposite)
            }
        }

        return buildMap {
            Quadrant.entries.forEach {
                val connectivity = if (opened) {
                    GridConnectivity.ALL
                } else {
                    getConnectivity(it)
                }

                put(
                    it, ComputeVoxel.create(context.blockPos, it, connectivity)
                )
            }
        }
    }
}

object ChestVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.block is AbstractChestBlock<*>
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val facing = context.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val type = context.blockState.getValue(BlockStateProperties.CHEST_TYPE)
        val blockingSide = getBlockingSide(facing, type)

        return buildMap {
            Quadrant.entries.forEach { quadrant ->
                val excludeList = buildList {
                    add(quadrant.x.opposite)
                    add(quadrant.y.opposite)
                    add(quadrant.z.opposite)
                    if (quadrant.x == blockingSide) {
                        add(quadrant.x)
                    }
                    if (quadrant.z == blockingSide) {
                        add(quadrant.z)
                    }
                }
                val connectivity = GridConnectivity.exclude(*excludeList.toTypedArray())
                put(quadrant, ComputeVoxel.create(context.blockPos, quadrant, connectivity))
            }
        }
    }

    private fun getBlockingSide(facing: Direction, type: ChestType): Direction? {
        return when (type) {
            ChestType.SINGLE -> null

            ChestType.LEFT -> {
                facing.counterClockWise
            }

            ChestType.RIGHT -> {
                facing.clockWise
            }
        }
    }
}

object SnowLayersVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.block is SnowLayerBlock
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        val layer = context.blockState.getValue(BlockStateProperties.LAYERS)
        return if (layer > 6) {
            SolidVoxelBlock.voxels(context)
        } else if (layer > 2) {
            buildMap {
                Quadrant.Regions.UP.forEach {
                    put(
                        it,
                        ComputeVoxel.create(context.blockPos, it, GridConnectivity.exclude(Direction.DOWN))
                    )
                }
                Quadrant.Regions.DOWN.forEach {
                    put(it, ComputeVoxel.create(context.blockPos, it, GridConnectivity.NONE))
                }
            }
        } else {
            buildMap {
                Quadrant.Regions.UP.forEach {
                    put(
                        it,
                        ComputeVoxel.create(context.blockPos, it, GridConnectivity.ALL)
                    )
                }
                Quadrant.Regions.DOWN.forEach {
                    put(
                        it,
                        ComputeVoxel.create(context.blockPos, it, GridConnectivity.exclude(Direction.DOWN))
                    )
                }
            }
        }
    }
}

object DirtPathVoxelBlock : VoxelBlock {
    override fun check(context: VoxelBlockContext): Boolean {
        return context.blockState.block is DirtPathBlock
    }

    override fun voxels(context: VoxelBlockContext): Map<Quadrant, ComputeVoxel> {
        return SolidVoxelBlock.voxels(context)
    }
}