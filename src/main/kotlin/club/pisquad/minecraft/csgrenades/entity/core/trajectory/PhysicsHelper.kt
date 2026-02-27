package club.pisquad.minecraft.csgrenades.entity.core.trajectory

import club.pisquad.minecraft.csgrenades.isBetween
import club.pisquad.minecraft.csgrenades.isPointWithinPlaneRange
import club.pisquad.minecraft.csgrenades.minus
import com.google.common.primitives.Doubles.max
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

object PhysicsHelper {

    /**Apply air drag and gravity to velocity
     * @param velocity Original velocity
     * @param partialTick between 0 and 1,
     * @return new velocity*/
    fun applyVelocityPhysics(velocity: Vec3, partialTick: Double): Vec3 {
        return velocity
    }

    fun getVelocityAfterBounce(position: Vec3, point: Vec3, velocity: Vec3, direction: Direction): Vec3 {
        val partialTick = velocity.length().div(position.distanceToSqr(position))
        val velocityAtBouncePoint = applyVelocityPhysics(velocity, partialTick)

        return when (direction.axis) {
            Direction.Axis.X -> {
                Vec3(-velocityAtBouncePoint.x, velocityAtBouncePoint.y, velocityAtBouncePoint.z)
            }

            Direction.Axis.Y -> {
                Vec3(velocityAtBouncePoint.x, -velocityAtBouncePoint.y, velocityAtBouncePoint.z)
            }

            Direction.Axis.Z -> {
                Vec3(velocityAtBouncePoint.x, velocityAtBouncePoint.y, -velocityAtBouncePoint.z)
            }
        }
    }

    fun getBlocksInPath(displacement: Segment): List<BlockPos> {
        val segments: MutableList<Segment> = mutableListOf()
        displacement.divideAlongAxis(Direction.Axis.X)
            .map { i -> i.divideAlongAxis(Direction.Axis.Y) }
            .forEach { j ->
                j.forEach {
                    segments.addAll(it.divideAlongAxis(Direction.Axis.Z))
                }
            }

        // We will sort it just in case
        return segments.map { it.midPoint() }.sortedBy { it.distanceToSqr(displacement.begin) }.map { BlockPos.containing(it) }
    }

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
                if (this.begin.x > aabb.maxX && this.end.x < aabb.maxX &&
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
                if (this.begin.x < aabb.minX && this.end.x > aabb.minX &&
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
                if (this.begin.y > aabb.maxY && this.end.y < aabb.maxY &&
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
                if (this.begin.y < aabb.minY && this.end.y > aabb.minY &&
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
                if (this.begin.z > aabb.maxZ && this.end.z < aabb.maxZ &&
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
                if (this.begin.z < aabb.minZ && this.end.z > aabb.minZ &&
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

        fun getPointByAxis(axis: Direction.Axis, position: Double): Vec3 {
            when (axis) {
                Direction.Axis.X -> {
                    if (position.isBetween(this.begin.x, this.end.x)) {
                        val c = (position - this.begin.x).div(this.end.x - this.begin.x)
                        return this.begin.add(this.direction().scale(c))
                    } else {
                        throw Exception("Invalid position")
                    }
                }

                Direction.Axis.Y -> {
                    if (position.isBetween(this.begin.y, this.end.y)) {
                        val c = (position - this.begin.y).div(this.end.y - this.begin.y)
                        return this.begin.add(this.direction().scale(c))
                    } else {
                        throw Exception("Invalid position")
                    }
                }

                Direction.Axis.Z -> {
                    if (position.isBetween(this.begin.z, this.end.z)) {
                        val c = (position - this.begin.z).div(this.end.z - this.begin.z)
                        return this.begin.add(this.direction().scale(c))
                    } else {
                        throw Exception("Invalid position")
                    }
                }
            }
        }

        fun direction(): Vec3 {
            return this.end.minus(this.begin).normalize()
        }

        fun midPoint(): Vec3 {
            return getPointByAxis(Direction.Axis.X, (this.begin.x + this.end.x).div(2.0))
        }
    }

}
