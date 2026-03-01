package club.pisquad.minecraft.csgrenades.entity.core.trajectory

import club.pisquad.minecraft.csgrenades.math.Segment
import club.pisquad.minecraft.csgrenades.toVec3
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

object BounceHelper {
    enum class BounceResultTypes {
        THROUGH,
        BOUNCE,
        HIT_ENTITY,
    }

    data class BounceResult(
        val type: BounceResultTypes,
        val bouncePoint: Vec3 = Vec3.ZERO,
        val newVelocity: Vec3 = Vec3.ZERO,
        val direction: Direction? = null,
        val tickDelta: Double = 0.0,
    )

    fun tryBounce(level: Level, blockPos: BlockPos, position: Vec3, deltaMovement: Vec3, velocity: Vec3): BounceResult {
        val blockState = level.getBlockState(blockPos)
        val (_, point, direction) = getFirstCollision(
            blockState.getCollisionShape(level, blockPos).toAabbs().map { it.move(blockPos.toVec3()) },
            position,
            deltaMovement,
        ) ?: return BounceResult(BounceResultTypes.THROUGH)
        val tickDelta = position.distanceTo(point).div(velocity.length())
//        if (tickDelta > 1) {
//            println("dd")
//        }
//        if (direction != Direction.UP) {
//            println("uiu")
//        }
        return BounceResult(
            BounceResultTypes.BOUNCE,
            point,
            PhysicsHelper.getVelocityAfterBounce(position, point, velocity, direction),
            direction,
            tickDelta,
        )
    }

    fun shouldBounceOnBlock(level: Level, blockPos: BlockPos): Boolean {
        return !level.getBlockState(blockPos).isAir
    }

    private fun getFirstCollision(aabbs: List<AABB>, position: Vec3, deltaMovement: Vec3): Triple<AABB, Vec3, Direction>? {
        val segment = Segment(position, position.add(deltaMovement))
        val candidates: MutableList<Triple<AABB, Vec3, Direction>> = mutableListOf()

        for (aabb in aabbs) {
            val (point, direction) = segment.intersectAabb(aabb) ?: continue
            candidates.add(Triple(aabb, point, direction))
        }
        candidates.sortBy { it.second.distanceTo(position) }
        val result = candidates.getOrNull(0)
        if (result != null && result.second.distanceTo(position) > deltaMovement.length()) {
            println("ddddd")
        }
        return result
    }
}
