package club.pisquad.minecraft.csgrenades.entity.core

import club.pisquad.minecraft.csgrenades.client.sound.CommonSoundEvents
import club.pisquad.minecraft.csgrenades.client.sound.GrenadeSoundData

class GrenadeEntitySoundEvents(
    val throwSoundEvent: GrenadeSoundData,
    val hitBlock: GrenadeSoundData,
    val hitEntity: GrenadeSoundData = CommonSoundEvents.hitEntity,
)
