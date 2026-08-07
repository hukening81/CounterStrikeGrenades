package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.config.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryConfig
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryDamageTypes
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovConfig
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovDamageTypes
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType

interface FireGrenadeConfig {
    val common: GrenadeCommonConfig
    val firegrenade: FireGrenadeCommonConfig
}

interface FireGrenadeDamageTypes {
    val fire: ResourceKey<DamageType>
    val common: GrenadeCommonDamageTypes
}

enum class FireGrenadeVariant(
    val getRandomParticleType: () -> ParticleOptions,
    val config: () -> FireGrenadeConfig,
    val damageTypes: () -> FireGrenadeDamageTypes,
) {
    INCENDIARY({
        FlameParticleRegistry.INCENDIARY_FLAME.get()
    }, { IncendiaryConfig }, { IncendiaryDamageTypes }),
    MOLOTOV({
        FlameParticleRegistry.MOLOTOV_FLAME.get()
    }, { MolotovConfig }, { MolotovDamageTypes }),
}
