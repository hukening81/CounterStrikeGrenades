package club.pisquad.minecraft.csgrenades.math

import club.pisquad.minecraft.csgrenades.div
import club.pisquad.minecraft.csgrenades.isBetween
import club.pisquad.minecraft.csgrenades.isPointWithinPlaneRange
import club.pisquad.minecraft.csgrenades.minus
import net.minecraft.core.Direction
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class Segment(
    val begin: Vec3,
    val end: Vec3,
) {
    val minX: Double
        get() {
            return min(begin.x, end.x)
        }
    val maxX: Double
        get() {
            return max(begin.x, end.x)
        }
    val minY: Double
        get() {
            return min(begin.y, end.y)
        }
    val maxY: Double
        get() {
            return max(begin.y, end.y)
        }
    val minZ: Double
        get() {
            return min(begin.z, end.z)
        }
    val maxZ: Double
        get() {
            return max(begin.z, end.z)
        }

    fun intersectAabb(aabb: AABB): Pair<Vec3, Direction>? {
        val candidates: MutableList<Pair<Vec3, Direction>> = mutableListOf()
        if (this.begin.x > this.end.x) {
            if (aabb.maxX.isBetween(this.begin.x, this.end.x) &&
                run {
                    val point = getPointByAxis(Direction.Axis.X, aabb.maxX)
                    return@run point.isPointWithinPlaneRange(Direction.Axis.X, aabb)
                }
            ) {
                candidates.add(
                    Pair(
                        getPointByAxis(Direction.Axis.X, aabb.maxX),
                        Direction.EAST,
                    ),
                )
            }
        } else {
            if (aabb.minX.isBetween(this.begin.x, this.end.x) &&
                run {
                    val point = getPointByAxis(Direction.Axis.X, aabb.minX)
                    return@run point.isPointWithinPlaneRange(Direction.Axis.X, aabb)
                }
            ) {
                candidates.add(
                    Pair(
                        getPointByAxis(Direction.Axis.X, aabb.minX),
                        Direction.WEST,
                    ),
                )
            }
        }
        if (this.begin.y > this.end.y) {
            if (this.maxY.isBetween(this.begin.y, this.end.y) &&
                run {
                    val point = getPointByAxis(Direction.Axis.Y, aabb.maxY)
                    return@run point.isPointWithinPlaneRange(Direction.Axis.Y, aabb)
                }
            ) {
                candidates.add(
                    Pair(
                        getPointByAxis(Direction.Axis.Y, aabb.maxY),
                        Direction.UP,
                    ),
                )
            }
        } else {
            if (aabb.minY.isBetween(this.begin.y, this.end.y) &&
                run {
                    val point = getPointByAxis(Direction.Axis.Y, aabb.minY)
                    return@run point.isPointWithinPlaneRange(Direction.Axis.Y, aabb)
                }
            ) {
                candidates.add(
                    Pair(
                        getPointByAxis(Direction.Axis.Y, aabb.minY),
                        Direction.DOWN,
                    ),
                )
            }
        }
        if (this.begin.z > this.end.z) {
            if (aabb.maxZ.isBetween(this.begin.z, this.end.z) &&
                run {
                    val point = getPointByAxis(Direction.Axis.Z, aabb.maxZ)
                    return@run point.isPointWithinPlaneRange(Direction.Axis.Z, aabb)
                }
            ) {
                candidates.add(
                    Pair(
                        getPointByAxis(Direction.Axis.Z, aabb.maxZ),
                        Direction.SOUTH,
                    ),
                )
            }
        } else {
            if (aabb.minZ.isBetween(this.begin.z, this.end.z) &&
                run {
                    val point = getPointByAxis(Direction.Axis.Z, aabb.minZ)
                    return@run point.isPointWithinPlaneRange(Direction.Axis.Z, aabb)
                }
            ) {
                candidates.add(
                    Pair(
                        getPointByAxis(Direction.Axis.Z, aabb.minZ),
                        Direction.NORTH,
                    ),
                )
            }
        }
        candidates.sortBy { it.first.distanceTo(this.begin) }
        return candidates.getOrNull(0)
    }


    fun divideAlongAxis(axis: Direction.Axis): List<Segment> {
        val result: MutableList<Segment> = mutableListOf()
        val (start, end) = when (axis) {
            Direction.Axis.X -> {
                Pair(
                    ceil(min(this.begin.x, this.end.x)).toInt(),
                    floor(max(this.begin.x, this.end.x)).toInt(),
                )

            }

            Direction.Axis.Y -> {
                Pair(
                    ceil(min(this.begin.y, this.end.y)).toInt(),
                    floor(max(this.begin.y, this.end.y)).toInt(),
                )


            }

            Direction.Axis.Z -> {
                Pair(
                    ceil(min(this.begin.z, this.end.z)).toInt(),
                    floor(max(this.begin.z, this.end.z)).toInt(),
                )

            }

        }

        var segment: Segment = this
        for (i in start..end) {
            val pair = segment.divide(axis, i.toDouble())
            result.add(pair.first)
            segment = pair.second
        }
        result.add(segment)
        return result
    }


    fun divide(axis: Direction.Axis, position: Double): Pair<Segment, Segment> {
        val point = getPointByAxis(axis, position)
        return Pair(Segment(this.begin, point), Segment(point, this.end))
    }

    fun length(): Double {
        return this.end.distanceTo(this.begin)
    }

    fun getPointByAxis(axis: Direction.Axis, position: Double): Vec3 {
        when (axis) {
            Direction.Axis.X -> {
                if (position.isBetween(this.begin.x, this.end.x)) {
                    val c = (position - this.begin.x).div(this.end.x - this.begin.x)
                    return this.begin.add(this.direction().scale(length()).scale(c))
                } else {
//                    throw Exception("Invalid position")
                    return this.begin
                }
            }

            Direction.Axis.Y -> {
                if (position.isBetween(this.begin.y, this.end.y)) {
                    val c = (position - this.begin.y).div(this.end.y - this.begin.y)
                    return this.begin.add(this.direction().scale(c))
                } else {
//                    throw Exception("Invalid position")
                    return this.begin
                }
            }

            Direction.Axis.Z -> {
                if (position.isBetween(this.begin.z, this.end.z)) {
                    val c = (position - this.begin.z).div(this.end.z - this.begin.z)
                    return this.begin.add(this.direction().scale(c))
                } else {
//                    throw Exception("Invalid position")
                    return this.begin
                }
            }
        }
    }

    fun direction(): Vec3 {
        return this.end.minus(this.begin).normalize()
    }

    fun midPoint(): Vec3 {
        return this.begin.add(this.end).div(2.0)
    }
}
