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

        while (partialTick < 1.0) {
            val subtickResult =
                predictSubtick(level, successResult.position, successResult.velocity, partialTick)
            successResult.updateFromSubtick(subtickResult)
            partialTick = subtickResult.partialTick
            if (count++ > MAXIMUM_SUBTICK_PREDICT) {
                return PredictResult.Error.MAX_SUBTICK_REACHED
            }
        }
        return successResult
    }


    private fun predictSubtick(
        level: Level,
        position: GrenadePosition,
        velocity: GrenadeVelocity,
        partialTick: Double
    ): PartialTickPredictResult {
        val from = position.center
        val deltaMovement = velocity.blocksPerTick.scale(1 - partialTick)
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
                                partialTick
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
                    PartialTickPredictResult.EntityHit.fromBlockHitResult(entity, result, from, velocity, partialTick)
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
            partialTick
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
        val partialTick: Double

        class Through(
            override val position: GrenadePosition,
            override val velocity: GrenadeVelocity,
            override val partialTick: Double,
        ) : PartialTickPredictResult {
            companion object {
                fun create(from: GrenadePosition, velocity: GrenadeVelocity, partialTick: Double): Through {
                    return Through(
                        from.move(velocity, GrenadeDuration.fromTick(1 - partialTick)),
                        PhysicsUtil.updateVelocityPartialTick(velocity, 1 - partialTick),
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
            override val partialTick: Double
        ) : PartialTickPredictResult {
            companion object {
                fun fromBlockHitResult(
                    entity: Entity,
                    result: BlockHitResult,
                    from: Vec3,
                    velocity: GrenadeVelocity,
                    partialTick: Double
                ): EntityHit {
                    val distance = result.location.distanceTo(from)
                    val partialTickDelta = distance / velocity.blocksPerTick.length()
                    val center =
                        from.add(velocity.blocksPerTick.scale(partialTickDelta - Double.epsilon()))
                    val velocityAtBounce = PhysicsUtil.updateVelocityPartialTick(velocity, partialTickDelta)

                    val data = GrenadeHitEntity(entity, result.location, result.direction, velocityAtBounce)
                    return EntityHit(
                        data,
                        distance,
                        GrenadePosition.fromCenter(center),
                        PhysicsUtil.bounceVelocity(velocityAtBounce, result.direction),
                        partialTick + partialTickDelta
                    )
                }
            }
        }

        class BlockHit(
            val data: GrenadeHitBlock,
            val distance: Double,
            override val position: GrenadePosition,
            override val velocity: GrenadeVelocity,
            override val partialTick: Double
        ) : PartialTickPredictResult {
            companion object {
                fun fromHitResult(
                    result: BlockHitResult,
                    from: Vec3,
                    velocity: GrenadeVelocity,
                    partialTick: Double
                ): BlockHit {
                    val deltaMovement = result.location.subtract(from)
                    val distance = deltaMovement.length()
                    val partialTickDelta = distance / velocity.blocksPerTick.length()
                    val center = from.add(deltaMovement.scale(1 - Double.epsilon()))
                    val velocityAtBounce = PhysicsUtil.updateVelocityPartialTick(velocity, partialTickDelta)
                    val newVelocity = PhysicsUtil.bounceVelocity(velocityAtBounce, result.direction)

                    val data = GrenadeHitBlock(result.blockPos, result.location, result.direction, newVelocity)
                    return BlockHit(
                        data,
                        distance,
                        GrenadePosition.fromCenter(center),
                        newVelocity,
                        partialTick + partialTickDelta
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
