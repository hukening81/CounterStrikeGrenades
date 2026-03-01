package club.pisquad.minecraft.csgrenades

import net.minecraft.core.Direction
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

fun Vec3.minus(other: Vec3): Vec3 = this.add(other.reverse())
fun Vec3.minus(x: Double, y: Double, z: Double): Vec3 = this.add(-x, -y, -z)

fun Vec3.div(factor: Double): Vec3 = this.scale(1.div(factor))
fun Vec3.minusLength(l: Double): Vec3 {
    val c = 1 - l.div(this.length())
    return this.scale(c)
}

fun Vec3.isPointWithinPlaneRange(
    excludeAxis: Direction.Axis,
    minX: Double, maxX: Double,
    minY: Double, maxY: Double,
    minZ: Double, maxZ: Double,
): Boolean {
    return when (excludeAxis) {
        Direction.Axis.X -> (this.y.isBetween(minY, maxY) && this.z.isBetween(minZ, maxZ))
        Direction.Axis.Y -> (this.x.isBetween(minX, maxX) && this.z.isBetween(minZ, maxZ))
        Direction.Axis.Z -> (this.x.isBetween(minX, maxX) && this.y.isBetween(minY, maxY))
    }
}

fun Vec3.isPointWithinPlaneRange(
    excludeAxis: Direction.Axis,
    aabb: AABB,
): Boolean {
    return this.isPointWithinPlaneRange(excludeAxis, aabb.minX, aabb.maxX, aabb.minY, aabb.maxY, aabb.minZ, aabb.maxZ)
}
