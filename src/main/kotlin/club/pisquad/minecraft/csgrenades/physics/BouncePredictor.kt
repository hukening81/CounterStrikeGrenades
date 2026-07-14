//package club.pisquad.minecraft.csgrenades.physics
//
//import com.mojang.datafixers.util.Either
//import net.minecraft.core.BlockPos
//import net.minecraft.world.entity.Entity
//import net.minecraft.world.entity.boss.enderdragon.EnderDragon
//import net.minecraft.world.level.Level
//import net.minecraft.world.phys.AABB
//import net.minecraft.world.phys.Vec3
//import kotlin.jvm.optionals.getOrNull
//
//object BouncePredictor {
//    sealed interface BounceResult {
//        object Miss : BounceResult {
//            override val distance: Double = Double.MAX_VALUE
//        }
//
//        class BlockBounce(
//            override val distance: Double,
//            val bouncePoint: Vec3,
//            val blockPos: BlockPos,
//        ) : BounceResult {
//            companion object {
//                fun fromBounce(from: Vec3, to: Vec3, point: Vec3): BlockBounce {
//                    TODO()
//                }
//            }
//        }
//
//        class EntityBounce(
//            override val distance: Double,
//            val bouncePoint: Vec3,
//            val entity: Entity,
//        ) : BounceResult {
//            companion object {
//                fun fromBounce(from: Vec3, to: Vec3, point: Vec3) {
//
//                }
//            }
//        }
//
//        val distance: Double
//    }
//
//    fun tryBounceBlock(
//        level: Level,
//        from: Vec3,
//        to: Vec3,
//        blockPos: BlockPos
//    ): Either<BounceResult.BlockBounce, BounceResult.Miss> {
//        val shape = level.getBlockState(blockPos).getCollisionShape(level, blockPos)
//
//        val result: Either<BounceResult.BlockBounce, BounceResult.Miss> = Either.right(BounceResult.Miss)
//
//        val blockHitResult = AABB.clip(shape.toAabbs(), from, to, blockPos)
//        if (blockHitResult != null) {
//
//        }
////        for (aabb in shape.toAabbs()) {
////            val point = aabb.clip(from,to).getOrNull()?:continue
////            val distance =
////
////        }
//        return result
//    }
//
//
////    enum class BounceResultTypes {
////        THROUGH,
////        BOUNCE,
////        HIT_ENTITY,
////    }
//
////    data class BounceResult(
////        val type: BounceResultTypes,
////        val bouncePoint: Vec3 = Vec3.ZERO,
////        val newVelocity: Vec3 = Vec3.ZERO,
////        val direction: Direction? = null,
////        val tickDelta: Double = 0.0,
////    )
////
////    fun tryBounce(level: Level, blockPos: BlockPos, from: Vec3, to: Vec3): Vec3? {
////        val blockState = level.getBlockState(blockPos)
////        val shape = blockState.getCollisionShape(level, blockPos)
////
////        var result: BounceResult = BounceResult.Miss()
////
////        for (aabb in shape.toAabbs()) {
////            val point = aabb.clip(from, to).getOrNull() ?: continue
////            val distance = point.distanceToSqr(from)
////            if (distance < result.distance) {
////                result = BounceResult.BlockBounce.fromBounce(from, to, point)
////            }
////        }
////
////        var entityNearest = Pair(Double.MAX_VALUE, Vec3.ZERO)
////        val worldFrom = GrenadePosition.fromCenter(from).worldPos
////        val worldTo = GrenadePosition.fromCenter(to).worldPos
////        level.getEntities(null, AABB(worldFrom, worldTo), BouncePredictor::shouldBounceOnEntity).forEach { entity ->
////            getEntityBoundingBoxes(entity).forEach { bb ->
////                val point = bb.clip(from, to).getOrNull() ?: return@forEach
////                val distance = point.distanceToSqr(from)
////                if (distance < entityNearest.first) {
////                    entityNearest = Pair(distance, point)
////                }
////            }
////        }
//
////        ProjectileUtil.getEntityHitResult()
////        val (_, point, direction) = getFirstCollision(
////            blockState.getCollisionShape(level, blockPos).toAabbs().map { it.move(blockPos.toVec3()) },
////            position,
////            deltaMovement,
////        ) ?: return BounceResult(BounceResultTypes.THROUGH)
////        val tickDelta = position.distanceTo(point).div(velocity.length())
////        return BounceResult(
////            BounceResultTypes.BOUNCE,
////            point,
////            PhysicsHelper.getVelocityAfterBounce(position, point, velocity, direction),
////            direction,
////            tickDelta,
////        )
//    }
//
//    fun shouldBounceOnBlock(level: Level, blockPos: BlockPos): Boolean {
//        return !level.getBlockState(blockPos).isAir
//    }
//
//    fun shouldBounceOnEntity(entity: Entity): Boolean {
//        return true
//    }
//
////    private fun getFirstCollision(
////        aabbs: List<AABB>,
////        position: Vec3,
////        deltaMovement: Vec3
////    ): Triple<AABB, Vec3, Direction>? {
////        val segment = Segment(position, position.add(deltaMovement))
////        val candidates: MutableList<Triple<AABB, Vec3, Direction>> = mutableListOf()
////
////        for (aabb in aabbs) {
////            val (point, direction) = segment.intersectAabb(aabb) ?: continue
////            candidates.add(Triple(aabb, point, direction))
////        }
////        candidates.sortBy { it.second.distanceTo(position) }
////        val result = candidates.getOrNull(0)
////        return result
////    }
//}
//
//private fun getEntityBoundingBoxes(entity: Entity): List<AABB> {
//    return when (entity) {
//        is EnderDragon -> {
//            entity.subEntities.map { it.boundingBox }
//        }
//
//        else -> {
//            listOf(entity.boundingBox)
//        }
//    }
//}
//
//fun Either<BouncePredictor.BounceResult.BlockBounce, BouncePredictor.BounceResult.Miss>.distance(): Double {
//    return this.map({ it.distance }, { it.distance })
//
//}
