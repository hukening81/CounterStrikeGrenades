package club.pisquad.minecraft.csgrenades.core

import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType

interface GrenadeCommonSounds {
    val draw: GrenadeSoundData
    val hitBlock: GrenadeSoundData
    val hitEntity: GrenadeSoundData
    val `throw`: GrenadeSoundData
    val pinPullStart: GrenadeSoundData
    val pinPull: GrenadeSoundData
}

interface GrenadeCommonDamageTypes {
    val hit: ResourceKey<DamageType>
}