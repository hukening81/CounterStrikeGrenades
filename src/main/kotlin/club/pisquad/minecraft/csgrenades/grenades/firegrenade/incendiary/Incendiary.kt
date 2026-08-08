package club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary

import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonSounds
import club.pisquad.minecraft.csgrenades.core.GrenadeProperties
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.core.sound.DistanceSegmentedSoundData
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeDamageTypes
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeRegistryHelper
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeSounds
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovSounds
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.EntityType
import net.minecraftforge.registries.RegistryObject


const val INCENDIARY_RESOURCE_KEY = "incendiary"


object IncendiaryProperties : GrenadeProperties {
    override val entity: RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> =
        FireGrenadeRegistryHelper.incendiaryEntity
    override val item: RegistryObject<out CounterStrikeGrenadeItem> = FireGrenadeRegistryHelper.incendiaryItem
    override val resourceKey: String = INCENDIARY_RESOURCE_KEY
    override val sounds: GrenadeCommonSounds = IncendiarySounds
    override val damageTypes: GrenadeCommonDamageTypes = IncendiaryDamageTypes
}

object IncendiaryDamageTypes : GrenadeCommonDamageTypes, FireGrenadeDamageTypes {
    override val fire = ModDamageTypes.registerSingle("incendiary/fire")
    override val common: GrenadeCommonDamageTypes = object : GrenadeCommonDamageTypes {
        override val hit: ResourceKey<DamageType> = ModDamageTypes.registerSingle("incendiary/hit")
    }
    override val hit = this.common.hit
}

object IncendiarySounds : GrenadeCommonSounds, FireGrenadeSounds {
    override val hitBlock = GrenadeSoundData.create("incendiary.hit_block")
    override val hitEntity: GrenadeSoundData = ModSoundEvents.HIT_ENTITY
    override val draw = GrenadeSoundData.create("incendiary.draw")
    override val pinPull = GrenadeSoundData.create("incendiary.pinpull")
    override val pinPullStart = GrenadeSoundData.create("incendiary.pinpull_start")
    override val `throw` = GrenadeSoundData.create("incendiary.throw")

    val detonate = GrenadeSoundData.create("incendiary.detonate")
    override val smash = MolotovSounds.smash

    override val pop = GrenadeSoundData.create("incendiary.pop")
    val detonateDistant = GrenadeSoundData.create("incendiary.detonate_distant")
    override val detonateAir = GrenadeSoundData.create("incendiary.detonate_air")
    override val fireLoop: RegistryObject<SoundEvent>
        get() {
            return MolotovSounds.fireLoop
        }
    override val fireLoopFadeOut: RegistryObject<SoundEvent>
        get() {
            return MolotovSounds.fireLoopFadeOut
        }
    override val detonateSegmented = DistanceSegmentedSoundData.createTwoPhasedExplosion(
        this.detonate, this.detonateDistant
    )
    override val extinguish: GrenadeSoundData
        get() {
            return MolotovSounds.extinguish
        }
}
