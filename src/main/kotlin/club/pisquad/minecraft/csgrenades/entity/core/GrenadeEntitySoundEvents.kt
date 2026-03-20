package club.pisquad.minecraft.csgrenades.entity.core

import club.pisquad.minecraft.csgrenades.registry.sounds.CommonSoundEvents
import club.pisquad.minecraft.csgrenades.registry.sounds.GrenadeSoundData

class GrenadeEntitySoundEvents(
    val throwSoundEvent: GrenadeSoundData,
    val hitBlock: GrenadeSoundData,
    val hitEntity: GrenadeSoundData = CommonSoundEvents.hitEntity,
)
