package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import club.pisquad.minecraft.csgrenades.utils.GridConnectivity
import club.pisquad.minecraft.csgrenades.utils.Quadrant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.max
import kotlin.math.min

/**
 * representing a single voxel, 1/8 of a block
 *
 * @property position voxel's position, different from BlockPos
 * @property debug Debug info
 */
@Serializable
open class Voxel(
    val position: VoxelPos,
    val debug: VoxelDebug? = null,
)

@Serializable
class VoxelMap(
    @Serializable(with = Vec3Serializer::class) val center: Vec3,
    val inner: Map<VoxelPos, Voxel>
) : Map<VoxelPos, Voxel> by inner {

    companion object {
    }

    @Transient
    val edges: Set<VoxelPos> by lazy { this.keys.filter { this.isEdge(it) }.toSet() }

    @Transient
    val specials: Set<VoxelPos> by lazy {
        if (this.hasDebug) {
            this.filter { (pos, voxel) -> voxel.debug!!.special }.keys
        } else {
            emptySet()
        }
    }

    @Transient
    val boundingBox: AABB by lazy {
        val firstWorldPos = this.edges.first().worldPos()
        var minX = firstWorldPos.x
        var maxX = firstWorldPos.x
        var minY = firstWorldPos.y
        var maxY = firstWorldPos.y
        var minZ = firstWorldPos.z
        var maxZ = firstWorldPos.z

        this.edges.forEach {
            val worldPos = it.worldPos()
            minX = min(minX, worldPos.x)
            maxX = max(maxX, worldPos.x)
            minY = min(minY, worldPos.y)
            maxY = max(maxY, worldPos.y + 0.5)
            minZ = min(minZ, worldPos.z + 0.5)
            maxZ = max(maxZ, worldPos.z + 0.5)
        }
        return@lazy AABB(minX, minY, minZ, maxX, maxY, maxZ)
    }

    @Transient
    val hasDebug: Boolean by lazy { this.values.all { it.debug != null } }

    fun isEdge(position: VoxelPos): Boolean {
        return if (this.containsKey(position)) {
            !position.neighbors.all { this.containsKey(it) }
        } else {
            false
        }
    }
}

@Serializable
class VoxelDebug(
    var special: Boolean,
    var parent: Direction?,
)


class ComputeVoxel(
    val position: VoxelPos,
    val connectivity: GridConnectivity,
    val special: Boolean = true,
    var intensity: Int = 0,
    val spreadDecay: Int = 1,
    var parent: Direction? = null
) {
    companion object {
        fun create(
            blockPos: BlockPos,
            quadrant: Quadrant,
            connectivity: GridConnectivity,
            special: Boolean = true
        ): ComputeVoxel {
            return ComputeVoxel(
                VoxelPos.fromBlockAndQuadrant(blockPos, quadrant),
                connectivity,
                special
            )
        }
    }

    fun triggerIntensityUpdate(direction: Direction, newIntensity: Int): Boolean {
        return if (connectivity.contains(direction) && newIntensity > intensity) {
            intensity = newIntensity
            parent = direction
            true
        } else {
            false
        }
    }

    fun neighborIntensity(direction: Direction): Int {
        return if (connectivity.contains(direction)) {
            max(this.intensity - spreadDecay, 0)
        } else {
            0
        }
    }

    fun toVoxel(debug: Boolean = false): Voxel {
        val debugInfo = if (debug) {
            VoxelDebug(this.special, this.parent)
        } else {
            null
        }
        return Voxel(
            this.position, debugInfo
        )
    }
}