package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelMap
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3
import java.util.*

object SmokeGrenadeUtils {
    fun spawnSmokeRegionEntity(
        level: ServerLevel,
        ownerUUID: UUID,
        variant: SmokeGrenadeVariant,
        voxelMap: VoxelMap
    ): SmokeRegionEntity? {
        val entity = SmokeRegistryHelper.smokeRegionEntity.get().create(level)?:return null
        entity.deltaMovement = Vec3.ZERO

        entity.ownerUUID = ownerUUID
        entity.variant = variant
        entity.voxelMap = voxelMap

        entity.boundingBox = voxelMap.boundingBox

        entity.setPos(voxelMap.center)


        level.addFreshEntity(entity)
        return entity
    }
}