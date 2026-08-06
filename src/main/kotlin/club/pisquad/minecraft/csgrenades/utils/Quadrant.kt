package club.pisquad.minecraft.csgrenades.utils

import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import kotlin.math.floor

enum class Quadrant(val z: Direction, val x: Direction, val y: Direction) {
    NEU(Direction.NORTH, Direction.EAST, Direction.UP),
    NWU(Direction.NORTH, Direction.WEST, Direction.UP),
    SEU(Direction.SOUTH, Direction.EAST, Direction.UP),
    SWU(Direction.SOUTH, Direction.WEST, Direction.UP),
    NED(Direction.NORTH, Direction.EAST, Direction.DOWN),
    NWD(Direction.NORTH, Direction.WEST, Direction.DOWN),
    SED(Direction.SOUTH, Direction.EAST, Direction.DOWN),
    SWD(Direction.SOUTH, Direction.WEST, Direction.DOWN);

    companion object {
        fun from(position: Vec3): Quadrant {
            val x = if (position.x - floor(position.x) > 0.5) {
                Direction.EAST
            } else {
                Direction.WEST
            }
            val y = if (position.y - floor(position.y) > 0.5) {
                Direction.UP
            } else {
                Direction.DOWN
            }
            val z = if (position.z - floor(position.z) > 0.5) {
                Direction.SOUTH
            } else {
                Direction.NORTH
            }

            return Quadrant.entries.find {
                it.x == x && it.y == y && it.z == z
            }!!

        }
    }

    fun oppositeY(): Quadrant {
        return entries.find {
            it.x == this.x && it.z == this.z && it.y == this.y.opposite
        }!!
    }

    fun oppositeX(): Quadrant {
        return entries.find {
            it.x.opposite == this.x && it.z == this.z && it.y == this.y
        }!!
    }

    fun oppositeZ(): Quadrant {
        return entries.find {
            it.x == this.x && it.z == this.z.opposite && it.y == this.y
        }!!
    }

    fun clockWise(): Quadrant {
        return entries.find {
            it.x == this.z.clockWise && it.z == this.x.clockWise && it.y == this.y
        }!!
    }

    fun counterClockWise(): Quadrant {
        return entries.find {
            it.x == this.z.counterClockWise && it.z == this.x.counterClockWise && it.y == this.y
        }!!
    }

    object Regions {
        val UP: Set<Quadrant> = entries.filter { it.y == Direction.UP }.toSet()
        val DOWN: Set<Quadrant> = entries.filter { it.y == Direction.DOWN }.toSet()
        val NORTH: Set<Quadrant> = entries.filter { it.z == Direction.NORTH }.toSet()
        val SOUTH: Set<Quadrant> = entries.filter { it.z == Direction.SOUTH }.toSet()
        val WEST: Set<Quadrant> = entries.filter { it.x == Direction.WEST }.toSet()
        val EAST: Set<Quadrant> = entries.filter { it.x == Direction.EAST }.toSet()

        fun fromDirection(direction: Direction): Set<Quadrant> {
            return when (direction) {
                Direction.DOWN -> DOWN
                Direction.UP -> UP
                Direction.NORTH -> NORTH
                Direction.SOUTH -> SOUTH
                Direction.WEST -> WEST
                Direction.EAST -> EAST
            }
        }

        fun fromDirection(d1: Direction, d2: Direction): Set<Quadrant> {
            require(d1.axis.isHorizontal)
            require(d2.axis.isHorizontal)
            require(d1.axis != d2.axis)

            return entries.filter {
                (it.x == d1 && it.z == d2) || (it.z == d1 && it.x == d2)
            }.toSet()
        }
    }
}