package club.pisquad.minecraft.csgrenades.entity.smokegrenade

import club.pisquad.minecraft.csgrenades.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.ChainBlock
import net.minecraft.world.level.block.FenceBlock
import net.minecraft.world.level.block.FenceGateBlock
import net.minecraft.world.level.block.IronBarsBlock
import net.minecraft.world.level.block.SignBlock
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.TrapDoorBlock
import net.minecraft.world.level.block.WallBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.level.block.state.properties.SlabType
import net.minecraft.world.level.block.state.properties.WallSide
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.Tags
import kotlin.math.pow
import kotlin.random.Random

private class BlockStateCache(val level: Level) {
    val cache: MutableMap<BlockPos, BlockState> = mutableMapOf()
    fun get(blockPos: BlockPos): BlockState {
        if (cache.containsKey(blockPos)) {
            return cache[blockPos]!!
        } else {
            val blockState = level.getBlockState(blockPos)
            cache[blockPos] = blockState
            return blockState
        }
    }
}

private class SmokeSpreadProbe(
    var position: Vec3,
    var velocity: Vec3,
    val cache: BlockStateCache,
) {
    fun distanceToSqr(other: SmokeSpreadProbe): Double = this.position.distanceToSqr(other.position)
    fun distanceToSqr(other: Vec3): Double = this.position.distanceToSqr(other)

    fun walk() {
        val target = this.position.add(this.velocity)
        val targetBlockPos = BlockPos.containing(target)
        if (targetBlockPos == BlockPos.containing(this.position) || cache.get(targetBlockPos).isAir) {
            this.position = target
            return
        }
    }
}

class SmokeSpreadCalculator(val level: ServerLevel, val center: Vec3) {
    val probeCount: Int = 10
    val velocityScale: Double = 1.5
    val walkIteration = 1000
    private val cache: BlockStateCache = BlockStateCache(level)

    companion object {
        private fun HashSet<SmokeSpreadProbe>.updateVelocity() {
            this.forEach { probe ->
                run {
                    val massCenter = this.filter { it.distanceToSqr(probe) < 1 }.map { it.position }.reduce { acc, vec3 -> acc.add(vec3) }.div(this.size.toDouble())
                    val force = 1.div(probe.distanceToSqr(massCenter).pow(2))
                    probe.velocity = probe.position.minus(massCenter).scale(force)
                }
            }
        }
    }

    fun getResult(): Set<Vec3> {
        if (!level.getBlockState(BlockPos.containing(center)).isAir) {
            return emptySet()
        }
        val probes = HashSet<SmokeSpreadProbe>(probeCount)
        val surroundAirBlocks = getSurroundingAirBlocks()
        if (surroundAirBlocks.isEmpty()) {
            return emptySet()
        }
        // Initialize all probes, spread them in nearby air blocks
        repeat(surroundAirBlocks.size) {
            repeat(probeCount.div(surroundAirBlocks.size)) { _ ->
                probes.add(SmokeSpreadProbe(getRandomLocationInsideBlock(surroundAirBlocks[it]), Vec3.ZERO, cache))
            }
        }
        probes.updateVelocity()
        repeat(walkIteration) {
            probes.forEach { it.walk() }
            probes.updateVelocity()
        }
        return probes.map { it.position }.toSet()
    }

    private fun getSurroundingAirBlocks(): List<BlockPos> {
        val blockAt = BlockPos.containing(center)
        val blockAtState = level.getBlockState(blockAt)

        // Will not generate smoke when inside a waterlogged block
        val waterlogged = blockAtState.getOptionalValue(BlockStateProperties.WATERLOGGED)
        if (waterlogged.isPresent && waterlogged.get()) {
            return emptyList()
        }

        when (blockAtState.block) {
            is AirBlock -> {
                return listOf(blockAt)
            }

            is FenceBlock, is FenceGateBlock -> {
                return blockAt.adjacent().toMutableList().filterAir(this.level)
            }

            is StairBlock -> {
                val result = mutableListOf<BlockPos>()

                // Add blockPos above or below
                when (blockAtState.getValue(BlockStateProperties.HALF)) {
                    Half.TOP -> {
                        blockAt.below()
                    }

                    Half.BOTTOM -> {
                        blockAt.above()
                    }
                }.takeIf { level.getBlockState(it).isAir }?.let { result.add(it) }

                // Add horizontal surrounding blockPoses
                when (blockAtState.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                    Direction.UP -> {
                        listOf() // Stairs can never face up
                    }

                    Direction.DOWN -> {
                        listOf() // Stairs can never face down
                    }

                    Direction.NORTH -> {
                        listOf(blockAt.south(), blockAt.west(), blockAt.east())
                    }

                    Direction.SOUTH -> {
                        listOf(blockAt.north(), blockAt.west(), blockAt.east())
                    }

                    Direction.WEST -> {
                        listOf(blockAt.north(), blockAt.south(), blockAt.east())
                    }

                    Direction.EAST -> {
                        listOf(blockAt.north(), blockAt.south(), blockAt.west())
                    }
                }.filterAir(level).let { result.addAll(it) }

                return result
            }

            is IronBarsBlock -> {
                val south = blockAtState.getValue(BlockStateProperties.SOUTH)
                val north = blockAtState.getValue(BlockStateProperties.NORTH)
                val east = blockAtState.getValue(BlockStateProperties.EAST)
                val west = blockAtState.getValue(BlockStateProperties.WEST)

                val corner = getGrenadeCornerType(blockAt, center)
                return ExtendableBlockState(north, south, west, east).nonBlockingAdjacentForCorner(blockAt, corner).toMutableList().filterAir(level)
            }

            is ChainBlock -> {
                return blockAt.adjacent().toMutableList().filterAir(level)
            }

            is SignBlock -> {
                return blockAt.adjacent().toMutableList().filterAir(level)
            }

            is TrapDoorBlock -> {
                val result = blockAt.adjacent().toMutableList()
                when (blockAtState.getValue(BlockStateProperties.OPEN)) {
                    true -> {
                        when (blockAtState.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                            Direction.UP -> {
                                throw Exception("Trapdoors should never face up")
                            }

                            Direction.DOWN -> {
                                throw Exception("Trapdoors should never face down")
                            }

                            Direction.NORTH -> {
                                result.remove(blockAt.south())
                            }

                            Direction.SOUTH -> {
                                result.remove(blockAt.north())
                            }

                            Direction.WEST -> {
                                result.remove(blockAt.east())
                            }

                            Direction.EAST -> {
                                result.remove(blockAt.west())
                            }
                        }
                    }

                    false -> {
                        when (blockAtState.getValue(BlockStateProperties.HALF)) {
                            Half.TOP -> {
                                result.remove(blockAt.above())
                            }

                            Half.BOTTOM -> {
                                result.remove(blockAt.below())
                            }
                        }
                    }
                }
                return result.filterAir(level)
            }

            is WallBlock -> {
                val corner = getGrenadeCornerType(blockAt, center)

                // Tall or low type will be treated as obstracting
                val west = blockAtState.getValue(BlockStateProperties.WEST_WALL) != WallSide.NONE
                val east = blockAtState.getValue(BlockStateProperties.EAST_WALL) != WallSide.NONE
                val north = blockAtState.getValue(BlockStateProperties.NORTH_WALL) != WallSide.NONE
                val south = blockAtState.getValue(BlockStateProperties.SOUTH_WALL) != WallSide.NONE

                return ExtendableBlockState(north, south, west, east).nonBlockingAdjacentForCorner(blockAt, corner).toMutableList().filterAir(level)
            }

            is SlabBlock -> {
                val adjacent = blockAt.adjacent().toMutableList()
                when (blockAtState.getValue(BlockStateProperties.SLAB_TYPE)) {
                    SlabType.TOP -> {
                        adjacent.remove(blockAt.above())
                        return adjacent.filterAir(level)
                    }

                    SlabType.BOTTOM -> {
                        adjacent.remove(blockAt.below())
                        return adjacent.filterAir(level)
                    }

                    SlabType.DOUBLE -> {
                        //                        Although
                        return emptyList()
                    }
                }
            }

            else -> {
                if (blockAtState.`is`(Tags.Blocks.GLASS_PANES)) {
                    val south = blockAtState.getValue(BlockStateProperties.SOUTH)
                    val north = blockAtState.getValue(BlockStateProperties.NORTH)
                    val east = blockAtState.getValue(BlockStateProperties.EAST)
                    val west = blockAtState.getValue(BlockStateProperties.WEST)

                    val corner = getGrenadeCornerType(blockAt, center)
                    return ExtendableBlockState(north, south, west, east).nonBlockingAdjacentForCorner(blockAt, corner).toMutableList().filterAir(level)
                } else {
                    // Unhandled situation, default to emptyList
                    return listOf()
                }
            }
        }
    }
}

private fun getRandomLocationInsideBlock(blockPos: BlockPos): Vec3 = Vec3(
    blockPos.x.toDouble(),
    blockPos.y.toDouble(),
    blockPos.z.toDouble(),
).add(
    Vec3(
        Random.nextDouble(),
        Random.nextDouble(),
        Random.nextDouble(),
    ),
)

//
// import club.pisquad.minecraft.csgrenades.config.ModConfig
// import net.minecraft.core.BlockPos
// import net.minecraft.core.Direction
// import net.minecraft.server.level.ServerLevel
// import net.minecraft.world.level.Level
// import net.minecraft.world.level.block.AirBlock
// import net.minecraft.world.level.block.ChainBlock
// import net.minecraft.world.level.block.FenceBlock
// import net.minecraft.world.level.block.FenceGateBlock
// import net.minecraft.world.level.block.IronBarsBlock
// import net.minecraft.world.level.block.SignBlock
// import net.minecraft.world.level.block.SlabBlock
// import net.minecraft.world.level.block.StairBlock
// import net.minecraft.world.level.block.TrapDoorBlock
// import net.minecraft.world.level.block.WallBlock
// import net.minecraft.world.level.block.state.properties.BlockStateProperties
// import net.minecraft.world.level.block.state.properties.Half
// import net.minecraft.world.level.block.state.properties.SlabType
// import net.minecraft.world.level.block.state.properties.WallSide
// import net.minecraft.world.phys.Vec3
// import net.minecraftforge.common.Tags
// import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.minus
// import kotlin.random.Random
//
// class SmokeGrenadeSpreadBlockCalculator(
//    val level: ServerLevel,
//    val position: Vec3,
// ) {
//
//    private class InitialSpreadCalculator {
//        val generateCycle: Int = 5
//        val blockPerCycle: Int = 1500
//        val stepPerMove: Int = 2
//
//        fun getResult(): Set<BlockPos> {
//            TODO()
//        }
//    }
//
//    fun getResult(): Set<BlockPos> {
//        val surroundingAirBlock = this.getSurroundingAirBlocks()
//
//        // use the air block that can generate the most initial smoke
//        val initialSmoke: Set<BlockPos> = surroundingAirBlock.map {
//            InitialSpreadCalculator().getResult()
//        }.sortedBy { it.size }.ifEmpty { listOf(emptySet()) }[0]
//
//        if (initialSmoke.isEmpty()) return emptySet()
//
//        val maxFallHeight = ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get()
//        val smokeColumns = initialSmoke.groupBy { Pair(it.x, it.z) }
//        val totalColumnCount = smokeColumns.size.coerceAtLeast(1) // Avoid division by zero
//
//        // --- Calculation Pass: Determine raw fall distance and support for each column ---
//        val columnFallInfo = mutableMapOf<Pair<Int, Int>, Int>() // Stores raw fall distance
//        var landingColumnCount = 0
//
//        return finalSmoke.toList()
//        TODO()
//    }
//
//    // Optimization: Use Sets instead of Lists to avoid duplicates from the start.
//    private fun calculate(level: Level): Set<BlockPos> {
//        val blocks = mutableSetOf<BlockPos>()
//        var generatedInLastCycle = mutableSetOf<BlockPos>(origin)
//        var generatedInCurrentCycle = mutableSetOf<BlockPos>()
//        repeat(generateCycle) {
//            repeat(blockPerCycle) {
//                generatedInLastCycle.random().let {
//                    var currentLocation = it
//                    repeat(stepPerMove) {
//                        currentLocation = randomMoveOnce(level, currentLocation)
//                    }
//                    generatedInCurrentCycle.add(currentLocation)
//                }
//            }
//            blocks.addAll(generatedInLastCycle)
//            generatedInLastCycle = generatedInCurrentCycle
//            generatedInCurrentCycle = mutableSetOf()
//        }
//        return blocks
//    }
//
//    private fun randomMoveOnce(level: Level, blockPos: BlockPos): BlockPos {
//        val newLocation = when (randomDirection()) {
//            Direction.UP -> blockPos.above()
//
//            // Corrected
//            Direction.DOWN -> blockPos.below()
//
//            // Corrected
//            Direction.NORTH -> blockPos.north()
//
//            Direction.SOUTH -> blockPos.south()
//
//            Direction.WEST -> blockPos.west()
//
//            Direction.EAST -> blockPos.east()
//        }
//        if (level.getBlockState(newLocation).getCollisionShape(level, newLocation).isEmpty) {
//            return newLocation
//        }
//        return blockPos
//    }
//
//    private fun randomDirection(): Direction = when ((Random.nextInt(10))) {
//        0 -> Direction.UP
//        1, 5 -> Direction.DOWN
//        2, 6 -> Direction.NORTH
//        3, 7 -> Direction.SOUTH
//        4, 8 -> Direction.WEST
//        else -> Direction.EAST
//    }
//
//    private fun getSurroundingAirBlocks(): List<BlockPos> {
//        val blockAt = BlockPos.containing(position)
//        val blockAtState = level.getBlockState(blockAt)
//
//        // Will not generate smoke when inside a waterlogged block
//        val waterlogged = blockAtState.getOptionalValue(BlockStateProperties.WATERLOGGED)
//        if (waterlogged.isPresent && waterlogged.get()) {
//            return emptyList()
//        }
//
//        when (blockAtState.block) {
//            is AirBlock -> {
//                return listOf(blockAt)
//            }
//
//            is FenceBlock, is FenceGateBlock -> {
//                return blockAt.adjacent().toMutableList().filterAir(this.level)
//            }
//
//            is StairBlock -> {
//                val result = mutableListOf<BlockPos>()
//
//                // Add blockPos above or below
//                when (blockAtState.getValue(BlockStateProperties.HALF)) {
//                    Half.TOP -> {
//                        blockAt.below()
//                    }
//
//                    Half.BOTTOM -> {
//                        blockAt.above()
//                    }
//                }.takeIf { level.getBlockState(it).isAir }?.let { result.add(it) }
//
//                // Add horizontal surrounding blockPoses
//                when (blockAtState.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
//                    Direction.UP -> {
//                        listOf() // Stairs can never face up
//                    }
//
//                    Direction.DOWN -> {
//                        listOf() // Stairs can never face down
//                    }
//
//                    Direction.NORTH -> {
//                        listOf(blockAt.south(), blockAt.west(), blockAt.east())
//                    }
//
//                    Direction.SOUTH -> {
//                        listOf(blockAt.north(), blockAt.west(), blockAt.east())
//                    }
//
//                    Direction.WEST -> {
//                        listOf(blockAt.north(), blockAt.south(), blockAt.east())
//                    }
//
//                    Direction.EAST -> {
//                        listOf(blockAt.north(), blockAt.south(), blockAt.west())
//                    }
//                }.filterAir(level).let { result.addAll(it) }
//
//                return result
//            }
//
//            is IronBarsBlock -> {
//                val south = blockAtState.getValue(BlockStateProperties.SOUTH)
//                val north = blockAtState.getValue(BlockStateProperties.NORTH)
//                val east = blockAtState.getValue(BlockStateProperties.EAST)
//                val west = blockAtState.getValue(BlockStateProperties.WEST)
//
//                val corner = getGrenadeCornerType(blockAt, position)
//                return ExtendableBlockState(north, south, west, east).nonBlockingAdjacentForCorner(blockAt, corner).toMutableList().filterAir(level)
//            }
//
//            is ChainBlock -> {
//                return blockAt.adjacent().toMutableList().filterAir(level)
//            }
//
//            is SignBlock -> {
//                return blockAt.adjacent().toMutableList().filterAir(level)
//            }
//
//            is TrapDoorBlock -> {
//                val result = blockAt.adjacent().toMutableList()
//                when (blockAtState.getValue(BlockStateProperties.OPEN)) {
//                    true -> {
//                        when (blockAtState.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
//                            Direction.UP -> {
//                                throw Exception("Trapdoors should never face up")
//                            }
//
//                            Direction.DOWN -> {
//                                throw Exception("Trapdoors should never face down")
//                            }
//
//                            Direction.NORTH -> {
//                                result.remove(blockAt.south())
//                            }
//
//                            Direction.SOUTH -> {
//                                result.remove(blockAt.north())
//                            }
//
//                            Direction.WEST -> {
//                                result.remove(blockAt.east())
//                            }
//
//                            Direction.EAST -> {
//                                result.remove(blockAt.west())
//                            }
//                        }
//                    }
//
//                    false -> {
//                        when (blockAtState.getValue(BlockStateProperties.HALF)) {
//                            Half.TOP -> {
//                                result.remove(blockAt.above())
//                            }
//
//                            Half.BOTTOM -> {
//                                result.remove(blockAt.below())
//                            }
//                        }
//                    }
//                }
//                return result.filterAir(level)
//            }
//
//            is WallBlock -> {
//                val corner = getGrenadeCornerType(blockAt, position)
//
//                // Tall or low type will be treated as obstracting
//                val west = blockAtState.getValue(BlockStateProperties.WEST_WALL) != WallSide.NONE
//                val east = blockAtState.getValue(BlockStateProperties.EAST_WALL) != WallSide.NONE
//                val north = blockAtState.getValue(BlockStateProperties.NORTH_WALL) != WallSide.NONE
//                val south = blockAtState.getValue(BlockStateProperties.SOUTH_WALL) != WallSide.NONE
//
//                return ExtendableBlockState(north, south, west, east)
//                    .nonBlockingAdjacentForCorner(blockAt, corner)
//                    .toMutableList().filterAir(level)
//            }
//
//            is SlabBlock -> {
//                val adjacent = blockAt.adjacent().toMutableList()
//                when (blockAtState.getValue(BlockStateProperties.SLAB_TYPE)) {
//                    SlabType.TOP -> {
//                        adjacent.remove(blockAt.above())
//                        return adjacent.filterAir(level)
//                    }
//
//                    SlabType.BOTTOM -> {
//                        adjacent.remove(blockAt.below())
//                        return adjacent.filterAir(level)
//                    }
//
//                    SlabType.DOUBLE -> {
// //                        Although
//                        return emptyList()
//                    }
//                }
//            }
//
//            else -> {
//                if (blockAtState.`is`(Tags.Blocks.GLASS_PANES)) {
//                    val south = blockAtState.getValue(BlockStateProperties.SOUTH)
//                    val north = blockAtState.getValue(BlockStateProperties.NORTH)
//                    val east = blockAtState.getValue(BlockStateProperties.EAST)
//                    val west = blockAtState.getValue(BlockStateProperties.WEST)
//
//                    val corner = getGrenadeCornerType(blockAt, position)
//                    return ExtendableBlockState(north, south, west, east).nonBlockingAdjacentForCorner(blockAt, corner).toMutableList().filterAir(level)
//                } else {
//                    // Unhandled situation, default to emptyList
//                    return listOf()
//                }
//            }
//        }
//    }
// }
//
enum class Corner(val main: Direction, val secondary: Direction) {
    SOUTHWEST(Direction.SOUTH, Direction.WEST),
    SOUTHEAST(Direction.SOUTH, Direction.EAST),
    NORTHWEST(Direction.NORTH, Direction.WEST),
    NORTHEAST(Direction.NORTH, Direction.EAST),
}

private class ExtendableBlockState(val north: Boolean, val south: Boolean, val west: Boolean, val east: Boolean) {
    fun get(direction: Direction): Boolean = when (direction) {
        Direction.DOWN -> throw Exception("No down property for glass pane")
        Direction.UP -> throw Exception("No up property for glass pane")
        Direction.NORTH -> this.north
        Direction.SOUTH -> this.south
        Direction.WEST -> this.west
        Direction.EAST -> this.east
    }

    fun nonBlockingAdjacentForCorner(blockPos: BlockPos, corner: Corner): Set<BlockPos> {
        val result = mutableSetOf<BlockPos>(blockPos.relative(corner.main), blockPos.relative(corner.secondary))
        if ((this.get(corner.secondary) && this.get(corner.main)) || (this.get(corner.secondary) && this.get(corner.secondary.opposite))) {
            // EMPTY
        } else {
            result.add(blockPos.relative(corner.main.opposite))
        }

        if ((this.get(corner.main) && this.get(corner.secondary)) || (this.get(corner.main) && this.get(corner.main.opposite))) {
            // EMPTY
        } else {
            result.add(blockPos.relative(corner.secondary.opposite))
        }

        return result
    }
}

fun getGrenadeCornerType(blockPos: BlockPos, position: Vec3): Corner {
    val relativePos = position.minus(blockPos.center)
    return if (relativePos.z > 0) {
        if (relativePos.x > 0) {
            Corner.SOUTHEAST
        } else {
            Corner.SOUTHWEST
        }
    } else {
        if (relativePos.x > 0) {
            Corner.NORTHEAST
        } else {
            Corner.NORTHWEST
        }
    }
}

fun List<BlockPos>.filterAir(level: Level): List<BlockPos> = this.filter { level.getBlockState(it).isAir }
fun BlockPos.adjacent(): Set<BlockPos> = setOf(
    this.above(),
    this.below(),
    this.north(),
    this.south(),
    this.west(),
    this.east(),
)
