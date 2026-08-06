package club.pisquad.minecraft.csgrenades.utils

import net.minecraft.core.Direction
import java.util.*
import java.util.function.IntFunction

class GridConnectivity private constructor(
    private val inner: EnumSet<Direction>
) : Set<Direction> by inner {

    @Deprecated("??")
    override fun <T : Any?> toArray(generator: IntFunction<Array<out T?>?>): Array<out T?>? {
        return generator.apply(0)
    }

    fun isBlocking(direction: Direction): Boolean {
        return !this.contains(direction)
    }

    companion object {
        val ALL = GridConnectivity(EnumSet.allOf(Direction::class.java))
        val NONE = GridConnectivity(EnumSet.noneOf(Direction::class.java))

        fun from(vararg directions: Direction): GridConnectivity {
            return GridConnectivity(EnumSet.copyOf(directions.toList()))
        }

        fun exclude(vararg excludes: Direction): GridConnectivity {
            val directions = Direction.entries.filterNot { excludes.contains(it) }
            return GridConnectivity(EnumSet.copyOf(directions))
        }
    }
}