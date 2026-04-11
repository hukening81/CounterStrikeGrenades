package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.network.serializer.BlockPosSerializer
import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.DoorHingeSide
import net.minecraft.world.phys.Vec3
import kotlin.math.max

@Serializable
sealed class VoxelState {
    /*Position of this voxel, corresponding to BlockPos*/
    abstract val position: @Serializable(with = BlockPosSerializer::class) BlockPos
    abstract var intensity: Int

    open fun isOccupied(): Boolean {
        return intensity > 0
    }

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
    abstract fun asOrigin(center: Vec3): List<Direction>

    abstract fun getNeighborIntensity(direction: Direction): Int

    abstract fun updateIntensity(direction: Direction, intensity: Int): Boolean

    companion object {
        fun getInitialIntensity(): Int {
            return ModConfig.smokegrenade.initialIntensity.get()
        }
    }
}

@Serializable
class AirVoxel(
    override val position: @Serializable(with = BlockPosSerializer::class) BlockPos
) : VoxelState() {
    override var intensity: Int = 0

    override fun isOccupied(): Boolean {
        return intensity > 0
    }

    override fun getNeighborIntensity(direction: Direction): Int {
        return max(0, intensity - 1)
    }

    override fun updateIntensity(direction: Direction, intensity: Int): Boolean {
        return if (intensity > this.intensity) {
            this.intensity = intensity
            true
        } else {
            false
        }
    }

    override fun asOrigin(center: Vec3): List<Direction> {
        this.intensity = getInitialIntensity()
        return Direction.entries
    }


}

@Serializable
class SolidVoxel(
    override val position: @Serializable(with = BlockPosSerializer::class) BlockPos
) : VoxelState() {
    override var intensity: Int = 0

    override fun isOccupied(): Boolean {
        return false
    }

    override fun getNeighborIntensity(direction: Direction): Int {
        return intensity
    }

    override fun updateIntensity(
        direction: Direction,
        intensity: Int
    ): Boolean {
        return false
    }

    override fun asOrigin(center: Vec3): List<Direction> {
        return emptyList()
    }
}

@Serializable
class DoorVoxel(
    override val position: @Serializable(with = BlockPosSerializer::class) BlockPos,
    val facing: Direction,
    val hinge: DoorHingeSide,
    val opened: Boolean,
) : VoxelState() {
    init {
        require(facing.axis.isHorizontal)
    }

    override var intensity: Int = 0

    override fun asOrigin(center: Vec3): List<Direction> {
        val blocking = getBlockingSide()
        intensity = getInitialIntensity()
        return Direction.entries.filterNot { it == blocking }
    }

    override fun getNeighborIntensity(direction: Direction): Int {
        return if (getBlockingSide() == direction) {
            0
        } else {
            max(intensity - 1, 0)
        }
    }

    override fun updateIntensity(
        direction: Direction,
        intensity: Int
    ): Boolean {
        if (getBlockingSide() == direction) {
            return false
        } else {
            if (intensity > this.intensity) {
                this.intensity = intensity
                return true
            } else {
                return false
            }
        }
    }

    fun getBlockingSide(): Direction {
        return if (this.opened) {
            if (this.hinge == DoorHingeSide.LEFT) {
                this.facing.opposite.clockWise
            } else {
                this.facing.opposite.counterClockWise
            }
        } else {
            facing.opposite
        }
    }
}