package club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov

import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonSounds
import club.pisquad.minecraft.csgrenades.core.GrenadeProperties
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadeRegistryHelper
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
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

object MolotovDamageTypes : GrenadeCommonDamageTypes {
    val fire = ModDamageTypes.registerSingle("molotov/fire")
    override val hit = ModDamageTypes.registerSingle("molotov/hit")
}

object MolotovSounds : GrenadeCommonSounds {
    override val draw = GrenadeSoundData.create("molotov.draw")
    override val `throw` = GrenadeSoundData.create("molotov.throw")
    override val hitBlock = GrenadeSoundData.create("molotov.bounce")
    override val hitEntity: GrenadeSoundData = ModSoundEvents.HIT_ENTITY
    override val pinPullStart = GrenadeSoundData.empty()

    val extinguish = GrenadeSoundData.create("molotov.extinguish")
    val fireIdle = GrenadeSoundData.create("molotov.fire_idle")
    override val pinPull = fireIdle
    val ignite = GrenadeSoundData.create("molotov.ignite")
    val fireLoop = GrenadeSoundData.create("molotov.fire_loop")
    val fireFadeout = GrenadeSoundData.create("molotov.fire_fadeout")
    val smash = GrenadeSoundData.create("molotov.smash")
    val detonate = GrenadeSoundData.create("molotov.detonate")
    val detonateDistant = GrenadeSoundData.create("molotov.detonate_distant")
    val detonateAir = GrenadeSoundData.create("molotov.detonate_air")
}
