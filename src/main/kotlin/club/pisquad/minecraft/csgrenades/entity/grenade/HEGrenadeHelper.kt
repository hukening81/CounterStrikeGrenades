package club.pisquad.minecraft.csgrenades.entity.grenade

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.entity.smokegrenade.SmokeGrenadeEntity
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

object HEGrenadeHelper {
    fun blowUpNearbySmokeGrenade(level: ClientLevel, center: Vec3) {
        val smokeRadius = ModConfig.SmokeGrenade.SMOKE_RADIUS.get()
        val heDamageRadius = ModConfig.HEGrenade.DAMAGE_RADIUS.get()
        val smokeFallingHeight = ModConfig.SmokeGrenade.SMOKE_MAX_FALLING_HEIGHT.get()
        level.getEntitiesOfClass(
            SmokeGrenadeEntity::class.java,
            AABB(BlockPos.containing(center)).inflate(heDamageRadius + smokeRadius, smokeFallingHeight.toDouble() + heDamageRadius, heDamageRadius + smokeRadius),
        ).forEach {
//            it.clearSmokeWithinRange(center, heDamageRadius + 2.5, true)
        }
    }
}
