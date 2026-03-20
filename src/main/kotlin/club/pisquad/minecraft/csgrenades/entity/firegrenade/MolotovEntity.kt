package club.pisquad.minecraft.csgrenades.entity.firegrenade

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.entity.core.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.entity.core.GrenadeEntitySoundEvents
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registry.damage.ModDamageTypes
import club.pisquad.minecraft.csgrenades.registry.sounds.ModSoundEvents
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class MolotovEntity(pEntityType: EntityType<out MolotovEntity>, pLevel: Level) : FireGrenadeEntity(
    pEntityType, pLevel, GrenadeType.MOLOTOV, ModConfig.molotov.grenadeCommonConfig.fuseTime.get().toTick().toInt(),
) {
    override val sounds = GrenadeEntitySoundEvents(
        ModSoundEvents.molotov.throwSound,
        ModSoundEvents.molotov.hitBlock,
    )
    override val damageTypes: GrenadeEntityDamageTypes = GrenadeEntityDamageTypes(
        ModDamageTypes.molotov.hit,
        ModDamageTypes.molotov.fire,
    )
}
