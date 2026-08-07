package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.config.GrenadeCommonConfig
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.core.sound.DistanceSegmentedSoundData
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryConfig
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryDamageTypes
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiarySounds
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovConfig
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovDamageTypes
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovSounds
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.damagesource.DamageType
import net.minecraftforge.registries.RegistryObject

interface FireGrenadeConfig {
    val common: GrenadeCommonConfig
    val firegrenade: FireGrenadeCommonConfig
}

interface FireGrenadeDamageTypes {
    val fire: ResourceKey<DamageType>
    val common: GrenadeCommonDamageTypes
}
interface FireGrenadeSounds {
    val smash: GrenadeSoundData
    val detonateSegmented: DistanceSegmentedSoundData
    val detonateAir: GrenadeSoundData
    val fireLoop: RegistryObject<SoundEvent>
    val fireLoopFadeOut: RegistryObject<SoundEvent>
    val extinguish: GrenadeSoundData
}

enum class FireGrenadeVariant(
    val getRandomParticleType: () -> ParticleOptions,
    val config: () -> FireGrenadeConfig,
    val damageTypes: () -> FireGrenadeDamageTypes,
    val sounds: () -> FireGrenadeSounds,
) {
    INCENDIARY({
        FlameParticleRegistry.INCENDIARY_FLAME.get()
    }, { IncendiaryConfig }, { IncendiaryDamageTypes }, { IncendiarySounds }),
    MOLOTOV({
        FlameParticleRegistry.MOLOTOV_FLAME.get()
    }, { MolotovConfig }, { MolotovDamageTypes }, { MolotovSounds }),
}
