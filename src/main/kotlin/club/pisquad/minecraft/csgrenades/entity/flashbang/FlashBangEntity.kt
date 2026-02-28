package club.pisquad.minecraft.csgrenades.entity.flashbang

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.entity.core.ActivateByFuseGrenadeEntity
import club.pisquad.minecraft.csgrenades.entity.core.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.entity.core.GrenadeEntitySoundEvents
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registry.damage.ModDamageTypes
import club.pisquad.minecraft.csgrenades.registry.sounds.FlashbangSoundEvents
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class FlashBangEntity(pEntityType: EntityType<out FlashBangEntity>, pLevel: Level) : ActivateByFuseGrenadeEntity(
    pEntityType, pLevel, GrenadeType.FLASH_BANG,
    ModConfig.flashbang.grenadeCommonConfig.fuseTime.get().toTick().toInt(),
) {
    override val sounds = GrenadeEntitySoundEvents(
        FlashbangSoundEvents.throwSound.get(),
        FlashbangSoundEvents.bounce.get(),
    )
    override val damageTypes: GrenadeEntityDamageTypes = GrenadeEntityDamageTypes(
        ModDamageTypes.flashbang.hit,
        ModDamageTypes.flashbang.hit,
    )

//    private fun calculateAffectedPlayers(): List<AffectedPlayerInfo> {
//        val level = this.level() as ServerLevel
//        return level.getPlayers { it.distanceToSqr(this.position()) < 256 * 256 }.map {
//            AffectedPlayerInfo(it.uuid, FlashbangEffectData.create(this.level(), this.position(), it))
//        }.filter { it.effectData.effectSustain > 0 }
//    }

}
