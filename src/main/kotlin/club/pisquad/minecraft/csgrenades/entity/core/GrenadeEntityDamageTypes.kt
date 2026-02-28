package club.pisquad.minecraft.csgrenades.entity.core

import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType

data class GrenadeEntityDamageTypes(
    val hit: ResourceKey<DamageType>,
    val main: ResourceKey<DamageType>,
)
