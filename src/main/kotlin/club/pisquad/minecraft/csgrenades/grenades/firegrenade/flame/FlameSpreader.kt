package club.pisquad.minecraft.csgrenades.grenades.firegrenade.flame

import club.pisquad.minecraft.csgrenades.grenades.firegrenade.flame.FlameMap.Entry
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.ComputeVoxel
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelPos
import club.pisquad.minecraft.csgrenades.utils.Quadrant
import club.pisquad.minecraft.csgrenades.utils.VoxelBlockContext
import club.pisquad.minecraft.csgrenades.utils.VoxelBlockDelegator
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.pow
import kotlin.random.Random


private class VoxelBlockCache() {
    val cachedBlocks: MutableMap<BlockPos, Map<Quadrant, ComputeVoxel>> = mutableMapOf()

    fun get(level: Level, voxelPos: VoxelPos): ComputeVoxel {
        val blockPos = voxelPos.blockPos()
        return this.cachedBlocks.getOrPut(blockPos) {
            val context = VoxelBlockContext(
                blockPos,
                level.getBlockState(blockPos),
                level
            )
            VoxelBlockDelegator.delegate(context).voxels(context)
        }.get(voxelPos.quadrant)!!
    }
}

private const val MAX_SPREAD_JUMP_VOXEL_COUNT = 2
private const val MAX_SPREAD_FALL_VOXEL_COUNT = 10
private const val MAX_FIRE_HEIGHT = 5
private const val FIRE_PARTICLE_LIFETIME_PER_VOXEL = 5
const val FIRE_PARTICLE_Y_SPEED = 0.5 / FIRE_PARTICLE_LIFETIME_PER_VOXEL

class FlameSpreader(
    val origin: Vec3,
    val radius: Double,
    val fuelAmount: Int,
) {
    val originVoxelPos = VoxelPos.containing(this.origin)
    private val voxelCache = VoxelBlockCache()

    fun spread(level: Level): FlameMap {

        val originVoxel = this.searchSurfaceBelow(level, this.voxelCache.get(level, this.originVoxelPos))
            ?:return FlameMap(this.originVoxelPos, this.radius, mutableMapOf())
        originVoxel.intensity = this.fuelAmount * 2
        val originFireHeight = this.calculateFireHeight(level, originVoxel, MAX_FIRE_HEIGHT)
        if (originFireHeight < 1.0) {
            return FlameMap(this.originVoxelPos, this.radius, mutableMapOf())
        }

        val updateVoxels: MutableSet<ComputeVoxel> = mutableSetOf(originVoxel)
        val nextUpdateVoxels: MutableSet<ComputeVoxel> = mutableSetOf()

        while (updateVoxels.isNotEmpty()) {
            updateVoxels.forEach { currentVoxel ->
                currentVoxel.connectivity.forEach { direction ->
                    if (direction.axis == Direction.Axis.Y) {
                        return@forEach
                    }
                    val targetVoxel = this.spreadHorizontalOnce(level, currentVoxel, direction)?:return@forEach
                    val updated = targetVoxel.triggerIntensityUpdate(
                        direction.opposite,
                        currentVoxel.neighborIntensity(direction)
                    )
                    if (updated) {
                        nextUpdateVoxels.add(targetVoxel)
                    }
                }
            }
            updateVoxels.clear()
            updateVoxels.addAll(nextUpdateVoxels)
            nextUpdateVoxels.clear()
        }
        val flameMap = FlameMap(this.originVoxelPos, this.radius, mutableMapOf())
        this.voxelCache.cachedBlocks.toMap().forEach { pos, block ->
            block.values.forEach { voxel ->
                if (voxel.intensity > 0) {
                    val fireHeight = this.calculateFireHeight(level, voxel, MAX_FIRE_HEIGHT)
                    flameMap.put(voxel.position, Entry(fireHeight))
                }
            }
        }
        return flameMap
    }

    private fun spreadHorizontalOnce(
        level: Level,
        currentVoxel: ComputeVoxel,
        direciton: Direction
    ): ComputeVoxel? {
        require(direciton.axis != Direction.Axis.Y)

        var jumpCount = 0
        var fallCount = 0

        var targetVoxel = this.voxelCache.get(level, currentVoxel.position.relative(direciton))
        if (targetVoxel.position.center.distanceToSqr(this.origin) > this.radius.pow(2)) {
            return null
        }

        if (targetVoxel.connectivity.contains(direciton.opposite)) {
            return this.searchSurfaceBelow(level, targetVoxel)
        } else {
            repeat(MAX_SPREAD_JUMP_VOXEL_COUNT) {
                val aboveCurrentVoxel = this.voxelCache.get(level, currentVoxel.position.up)
                val targetVoxel = this.voxelCache.get(level, aboveCurrentVoxel.position.relative(direciton))
                if (aboveCurrentVoxel.connectivity.contains(direciton)
                    && targetVoxel.connectivity.contains(
                        direciton.opposite
                    )
                ) {
                    return this.searchSurfaceBelow(level, targetVoxel)
                }
            }
        }
        return null
    }

    private fun searchSurfaceBelow(level: Level, currentVoxel: ComputeVoxel): ComputeVoxel? {
        var targetVoxel = currentVoxel
        var belowTargetVoxel = this.voxelCache.get(level, targetVoxel.position.down)
        repeat(MAX_SPREAD_FALL_VOXEL_COUNT) {
            if (!targetVoxel.connectivity.contains(Direction.DOWN)
                ||
                !belowTargetVoxel.connectivity.contains(Direction.UP)
            ) {
                return targetVoxel
            }
            targetVoxel = belowTargetVoxel
            belowTargetVoxel = this.voxelCache.get(level, targetVoxel.position.down)
        }
        return null
    }

    private fun calculateFireHeight(level: Level, voxel: ComputeVoxel, maxHeiht: Int): Int {
        var currentVoxel = voxel
        var aboveVoxel = this.voxelCache.get(level, currentVoxel.position.up)

        repeat(maxHeiht) {
            if (currentVoxel.connectivity.contains(Direction.UP) && aboveVoxel.connectivity.contains(Direction.DOWN)) {
                currentVoxel = aboveVoxel
                aboveVoxel = this.voxelCache.get(level, aboveVoxel.position.up)
            } else {
                return it
            }
        }
        return maxHeiht
    }
}

@Serializable
data class FlameMap(
    val origin: VoxelPos,
    val radius: Double,
    val inner: MutableMap<VoxelPos, Entry>,
) : MutableMap<VoxelPos, Entry> by inner {
    @Serializable
    class Entry(
        val fireHeight: Int,
    )

    @Transient
    val boxes: Set<AABB> by lazy {
        this.inner.map { (pos, entry) ->
            val worldPos = pos.worldPos()
            AABB(
                worldPos.x,
                worldPos.y,
                worldPos.z,
                worldPos.x + 0.5,
                worldPos.y + entry.fireHeight * 0.5,
                worldPos.z + 0.5
            )
        }.toSet()
    }

    @Transient
    val boundingBox: AABB by lazy {
        if (this.boxes.isEmpty()) {
            return@lazy this.origin.boundibgBox
        }
        var bb = this.boxes.first()
        this.boxes.forEach {
            bb = bb.minmax(it)
        }
        bb
    }

    @Transient
    private val particleLifeTimeCache: MutableMap<VoxelPos, Int> = mutableMapOf()

    fun getParticleLifeTime(voxelPos: VoxelPos): Int {
        require(this.inner.containsKey(voxelPos))
        return this.particleLifeTimeCache.getOrPut(voxelPos) {
            val voxelHeight = ceil(
                Mth.lerp(
                    voxelPos.center.subtract(this.origin.center).horizontalDistance() / this.radius,
                    MAX_FIRE_HEIGHT.toDouble(),
                    1.0
                )
            ).toInt()
            max(voxelHeight, this.inner[voxelPos]!!.fireHeight) * FIRE_PARTICLE_LIFETIME_PER_VOXEL
        } + Random.nextInt(-3, 3)
    }
}