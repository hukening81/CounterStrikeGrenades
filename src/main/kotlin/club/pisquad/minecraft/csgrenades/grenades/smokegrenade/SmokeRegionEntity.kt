package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import java.util.UUID

class SmokeRegionEntity(entityType: EntityType<SmokeRegionEntity>, level: Level) : Entity(entityType, level) {

    lateinit var ownerUUID: UUID

    override fun defineSynchedData() {
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag) {
    }
}