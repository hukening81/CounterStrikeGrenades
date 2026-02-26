package club.pisquad.minecraft.csgrenades.entity.core.trajectory

import club.pisquad.minecraft.csgrenades.GRENADE_ENTITY_SIZE_HALF
import club.pisquad.minecraft.csgrenades.addGrenadeSizeOffset
import club.pisquad.minecraft.csgrenades.minusGrenadeSizeOffset
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.entity.IEntityAdditionalSpawnData


/**Helper class that implements custom physics required by grenades
 * It updates vanilla `position` and `deltaMovement`, alongside the provided `center` and `velocity`
 * */
abstract class CustomTrajectoryEntity(pEntityType: EntityType<out CustomTrajectoryEntity>, pLevel: Level) : Entity(pEntityType, pLevel),
    IEntityAdditionalSpawnData {

    var center: Vec3
        get() {
            return this.position().addGrenadeSizeOffset()
        }
        set(newCenter) {
            val position = newCenter.minusGrenadeSizeOffset()
            this.setPos(position)
        }
    var centerOld: Vec3
        get() {
            return Vec3(
                this.xo, this.yo, this.zo,
            ).add(GRENADE_ENTITY_SIZE_HALF, GRENADE_ENTITY_SIZE_HALF, GRENADE_ENTITY_SIZE_HALF)
        }
        set(newCenter) {
            val offset = newCenter.minusGrenadeSizeOffset()
            this.xOld = offset.x
            this.yOld = offset.y
            this.zOld = offset.z
            this.xo = offset.x
            this.yo = offset.y
            this.zo = offset.z
        }

    // Velocity is different from deltaMovement, latter one is the displacement between ticks
    val velocity: Vec3
        get() {
            return trajectory.velocity
        }

    var trajectory: Trajectory = Trajectory(Vec3.ZERO, Vec3.ZERO)

    override fun defineSynchedData() {
        TODO("Not yet implemented")
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag?) {
        TODO("Not yet implemented")
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag?) {
        TODO("Not yet implemented")
    }

}
