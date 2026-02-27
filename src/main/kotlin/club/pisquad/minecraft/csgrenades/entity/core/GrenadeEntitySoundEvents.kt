package club.pisquad.minecraft.csgrenades.entity.core

import club.pisquad.minecraft.csgrenades.registry.sounds.CommonSoundEvents
import net.minecraft.sounds.SoundEvent

class GrenadeEntitySoundEvents(
    val throwSoundEvent: SoundEvent,
    val bounce: SoundEvent,
    val hitEntity: SoundEvent = CommonSoundEvents.hitEntity.get(),
)
