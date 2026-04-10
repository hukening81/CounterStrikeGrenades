package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.network.serializer.BlockPosSerializer
import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import kotlin.math.max

@Serializable
sealed interface VoxelState {

    /*Position of this voxel, corresponding to BlockPos*/
    val position: BlockPos

    fun isOccupied(): Boolean

    fun voxelIntensity(): Int

    /**
     * Use this voxel as origin
     *
     * different types of block might have different behavior
     * - Closed trapdoors may not want to spread vertically
     * - Glass panes may only want to spread horizontally in a few direction
     *
     * @param center exact location of the smoke grenade
     * @return A list of directions this voxel can spread to when used as origin
     */
    fun asOrigin(center: Vec3): List<Direction>

    fun getNeighborIntensity(direction: Direction): Int

    fun updateNeighborIntensity(direction: Direction, intensity: Int): Boolean
}

@Serializable
class AirVoxel(
    override val position: @Serializable(with = BlockPosSerializer::class) BlockPos
) : VoxelState {
    var intensity: Int = 0

    override fun isOccupied(): Boolean {
        return intensity > 0
    }

    override fun voxelIntensity(): Int {
        return intensity
    }

    override fun getNeighborIntensity(direction: Direction): Int {
        return max(0, intensity - 1)
    }

    override fun updateNeighborIntensity(direction: Direction, intensity: Int): Boolean {
        return if (intensity > this.intensity) {
            this.intensity = intensity
            true
        } else {
            false
        }
    }

    override fun asOrigin(center: Vec3): List<Direction> {
        this.intensity = ModConfig.smokegrenade.initialIntensity.get()
        return Direction.entries
    }
}

@Serializable
class SolidVoxel(
    override val position: @Serializable(with = BlockPosSerializer::class) BlockPos
) : VoxelState {

    override fun isOccupied(): Boolean {
        return false
    }

    override fun voxelIntensity(): Int {
        return 0
    }

    override fun getNeighborIntensity(direction: Direction): Int {
        return 0
    }

    override fun updateNeighborIntensity(
        direction: Direction,
        intensity: Int
    ): Boolean {
        return false
    }

    override fun asOrigin(center: Vec3): List<Direction> {
        return emptyList()
    }
}