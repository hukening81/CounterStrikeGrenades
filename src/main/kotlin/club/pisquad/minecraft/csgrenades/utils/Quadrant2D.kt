package club.pisquad.minecraft.csgrenades.utils

import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import kotlin.math.floor

enum class Quadrant2D(
    val x: Direction,
    val z: Direction
) {
    NORTHWEST(Direction.NORTH, Direction.WEST),
    NORTHEAST(Direction.NORTH, Direction.EAST),
    SOUTHWEST(Direction.SOUTH, Direction.WEST),
    SOUTHEAST(Direction.SOUTH, Direction.EAST);

    companion object {
        fun from(center: Vec3): Quadrant2D {
            return if (center.x - floor(center.x) > 0.5) {
                if ((center.z - floor(center.z)) > 0.5) {
                    NORTHEAST
                } else {
                    NORTHWEST
                }
            } else {
                if ((center.z - floor(center.z)) > 0.5) {
                    SOUTHEAST
                } else {
                    SOUTHWEST
                }
            }
        }
    }
}