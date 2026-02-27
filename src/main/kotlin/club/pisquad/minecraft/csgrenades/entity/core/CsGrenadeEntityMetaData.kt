package club.pisquad.minecraft.csgrenades.entity.core

import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.damagesource.DamageType

class CsGrenadeEntityMetaData(
    val spawnSoundEvent: SoundEvent,
    val hitBlockSoundEvent: SoundEvent,

    val hitDamageType: ResourceKey<DamageType>,
    val mainDamageType: ResourceKey<DamageType>,
)
