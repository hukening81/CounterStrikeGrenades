package club.pisquad.minecraft.csgrenades.physics

import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.epsilon
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.boss.enderdragon.EnderDragon
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import kotlin.math.absoluteValue

object MovementPredictor {
    /*Predict the next tick*/
    private const val MAXIMUM_SUBTICK_PREDICT = 100
    fun predict(
        level: Level,
        position: GrenadePosition,
        velocity: GrenadeVelocity,
    ): PredictResult {
        var partialTick = 0.0
        var count = 0

        val successResult =
            PredictResult.PredictSuccess(
                position,
                velocity,
                mutableListOf(),
                mutableListOf(),
            )

        val getTickDeltaBeforeVerticalStop = {
            if (successResult.velocity.metersPerSecond.y > 0) {
                val k =
                    successResult.velocity.blocksPerTick.y / (-PhysicsUtil.VERTICAL_ACCELERATION_PER_TICK * (1 - partialTick)).absoluteValue
                if (k < 1.0) {
                    successResult.velocity.blocksPerTick.y / (-PhysicsUtil.VERTICAL_ACCELERATION_PER_TICK)
                } else {
                    1 - partialTick
                }
            } else {
                1 - partialTick
            }
        }

        while (partialTick < 1.0) {
            val subtickResult =
                predictPartialTick(
                    level,
                    successResult.position,
                    successResult.velocity,
                    getTickDeltaBeforeVerticalStop()
                )
            successResult.updateFromSubtick(subtickResult)
            partialTick += subtickResult.tickDelta
            if (count++ > MAXIMUM_SUBTICK_PREDICT) {
                return PredictResult.Error.MAX_SUBTICK_REACHED
            }
        }
        return successResult
    }


    private fun predictPartialTick(
        level: Level,
        position: GrenadePosition,
        velocity: GrenadeVelocity,
        tickDelta: Double
    ): PartialTickPredictResult {
        val from = position.center
        val deltaMovement = velocity.blocksPerTick.scale(tickDelta)
        val to = from.add(deltaMovement)

        //region Detect block collision
        val blockPredictResult: PartialTickPredictResult.BlockHit? =
            BlockGetter.traverseBlocks<PartialTickPredictResult.BlockHit, Unit>(
                from,
                to,
                Unit,
                { _, blockPos ->
                    if (shouldBounceOnBlock(level, blockPos)) {
                        val shape = level.getBlockState(blockPos).getCollisionShape(level, blockPos)
                        val hitResult = AABB.clip(shape.toAabbs(), from, to, blockPos)
                        if (hitResult != null) {
                            return@traverseBlocks PartialTickPredictResult.BlockHit.fromHitResult(
                                hitResult,
                                from,
                                velocity,
                            )
                        }
                    }
                    null
                }, { _ -> null })
        //endregion

        //region Detect entity collision
        var entityPredictResult: PartialTickPredictResult.EntityHit? = null
        val entities = level.getEntities(
            null,
            AABB.unitCubeFromLowerCorner(
                GrenadePosition.fromCenter(from).worldPos
            ).minmax(
                AABB.unitCubeFromLowerCorner(GrenadePosition.fromCenter(to).worldPos)
            ),
        ) { entity ->
            shouldBounceOnEntity(entity)
        }
        for (entity in entities) {
            val boxes = getEntityBoundingBoxes(entity).map { it.move(entity.position()) }
            val result = AABB.clip(boxes, from, to, BlockPos.ZERO)
            if (result != null) {
                val result =
                    PartialTickPredictResult.EntityHit.fromBlockHitResult(entity, result, from, velocity)
                if (entityPredictResult == null || entityPredictResult.distance > result.distance) {
                    entityPredictResult = result
                }
            }
        }
        //endregion
        return if (blockPredictResult != null) {
            if (entityPredictResult != null) {
                if (blockPredictResult.distance < entityPredictResult.distance) {
                    blockPredictResult
                } else {
                    entityPredictResult
                }
            } else {
                blockPredictResult
            }
        } else entityPredictResult ?: PartialTickPredictResult.Through.create(
            GrenadePosition.fromCenter(from),
            velocity,
            tickDelta
        )
    }

    sealed interface PredictResult {
        enum class Error : PredictResult {
            MAX_SUBTICK_REACHED
        }

        class PredictSuccess(
            var position: GrenadePosition,
            var velocity: GrenadeVelocity,
            val entityHits: MutableList<GrenadeHitEntity>,
            val blockHits: MutableList<GrenadeHitBlock>,
        ) : PredictResult {
            fun updateFromSubtick(result: PartialTickPredictResult) {
                this.position = result.position
                this.velocity = result.velocity

                when (result) {
                    is PartialTickPredictResult.BlockHit -> {
                        this.blockHits.add(result.data)
                    }

                    is PartialTickPredictResult.EntityHit -> {
                        this.entityHits.add(result.data)
                    }

                    is PartialTickPredictResult.Through -> {

                    }
                }
            }
        }
    }

    sealed interface PartialTickPredictResult {
        val position: GrenadePosition
        val velocity: GrenadeVelocity
        val tickDelta: Double

        class Through(
            override val position: GrenadePosition,
            override val velocity: GrenadeVelocity,
            override val tickDelta: Double,
        ) : PartialTickPredictResult {
            companion object {
                fun create(from: GrenadePosition, velocity: GrenadeVelocity, tickDelta: Double): Through {
                    return Through(
                        from.move(velocity, GrenadeDuration.fromTick(tickDelta)),
                        PhysicsUtil.updateVelocityPartialTick(velocity, tickDelta),
                        Double.MAX_VALUE
                    )
                }
            }
        }

        class EntityHit(
            val data: GrenadeHitEntity,
            val distance: Double,
            override val position: GrenadePosition,
            override val velocity: GrenadeVelocity,
            override val tickDelta: Double
        ) : PartialTickPredictResult {
            companion object {
                fun fromBlockHitResult(
                    entity: Entity,
                    result: BlockHitResult,
                    from: Vec3,
                    velocity: GrenadeVelocity,
                ): EntityHit {
                    val distance = result.location.distanceTo(from)
                    val tickDelta = distance / velocity.blocksPerTick.length()
                    val center =
                        from.add(velocity.blocksPerTick.scale(tickDelta - Double.epsilon()))
                    val velocityAtBounce = PhysicsUtil.updateVelocityPartialTick(velocity, tickDelta)

                    val data = GrenadeHitEntity(entity, result.location, result.direction, velocityAtBounce)
                    return EntityHit(
                        data,
                        distance,
                        GrenadePosition.fromCenter(center),
                        PhysicsUtil.bounceVelocity(velocityAtBounce, result.direction),
                        tickDelta
                    )
                }
            }
        }

        class BlockHit(
            val data: GrenadeHitBlock,
            val distance: Double,
            override val position: GrenadePosition,
            override val velocity: GrenadeVelocity,
            override val tickDelta: Double
        ) : PartialTickPredictResult {
            companion object {
                fun fromHitResult(
                    result: BlockHitResult,
                    from: Vec3,
                    velocity: GrenadeVelocity,
                ): BlockHit {
                    val deltaMovement = result.location.subtract(from)
                    val distance = deltaMovement.length()
                    val tickDelta = distance / velocity.blocksPerTick.length()
                    val center = from.add(deltaMovement.scale(1 - Double.epsilon()))
                    val velocityAtBounce = PhysicsUtil.updateVelocityPartialTick(velocity, tickDelta)
                    val newVelocity = PhysicsUtil.bounceVelocity(velocityAtBounce, result.direction)

                    val data = GrenadeHitBlock(result.blockPos, result.location, result.direction, newVelocity)
                    return BlockHit(
                        data,
                        distance,
                        GrenadePosition.fromCenter(center),
                        newVelocity,
                        tickDelta
                    )
                }
            }

        }
    }
}

private fun shouldBounceOnBlock(level: Level, blockPos: BlockPos): Boolean {
    return !level.getBlockState(blockPos).isAir
}

@Suppress("UNUSED_PARAMETER")
private fun shouldBounceOnEntity(entity: Entity): Boolean {
    return entity !is CounterStrikeGrenadeEntity
}

private fun getEntityBoundingBoxes(entity: Entity): List<AABB> {
    return when (entity) {
        is EnderDragon -> {
            entity.subEntities.map { it.boundingBox }
        }

        else -> {
            listOf(entity.boundingBox)
        }
    }
}
