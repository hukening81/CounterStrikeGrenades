package club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov

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
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.EntityType
import net.minecraftforge.registries.RegistryObject

const val MOLOTOV_RESOURCE_KEY = "molotov"


object MolotovProperties : GrenadeProperties {
    override val entity: RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> =
        FireGrenadeRegistryHelper.molotovEntity
    override val item: RegistryObject<out CounterStrikeGrenadeItem> = FireGrenadeRegistryHelper.molotovItem
    override val resourceKey: String = MOLOTOV_RESOURCE_KEY
    override val sounds: GrenadeCommonSounds = MolotovSounds
    override val damageTypes: GrenadeCommonDamageTypes = MolotovDamageTypes
}

object MolotovDamageTypes : GrenadeCommonDamageTypes, FireGrenadeDamageTypes {
    override val fire = ModDamageTypes.registerSingle("molotov/fire")
    override val common: GrenadeCommonDamageTypes = object : GrenadeCommonDamageTypes {
        override val hit: ResourceKey<DamageType> = ModDamageTypes.registerSingle("molotov/hit")
    }
    override val hit = this.common.hit
}

object MolotovSounds : GrenadeCommonSounds, FireGrenadeSounds {
    override val draw = GrenadeSoundData.create("molotov.draw")
    override val `throw` = GrenadeSoundData.create("molotov.throw")
    override val hitBlock = GrenadeSoundData.create("molotov.bounce")
    override val hitEntity: GrenadeSoundData = ModSoundEvents.HIT_ENTITY
    override val pinPullStart = GrenadeSoundData.empty()

    override val extinguish = GrenadeSoundData.create("molotov.extinguish")
    val fireIdle = GrenadeSoundData.create("molotov.fire_idle")
    override val pinPull = fireIdle
    val ignite = GrenadeSoundData.create("molotov.ignite")
    override val fireLoop = ModSoundEvents.registerSoundEvent("molotov.fire_loop")
    override val fireLoopFadeOut = ModSoundEvents.registerSoundEvent("molotov.fire_loop_fadeout")
    override val smash = GrenadeSoundData.create("molotov.smash")
    val detonate = GrenadeSoundData.create("molotov.detonate")
    val detonateDistant = GrenadeSoundData.create("molotov.detonate_distant")
    override val detonateSegmented = DistanceSegmentedSoundData.createTwoPhasedExplosion(
        this.detonate, this.detonateDistant
    )
    override val detonateAir = GrenadeSoundData.create("molotov.detonate_air")
}
