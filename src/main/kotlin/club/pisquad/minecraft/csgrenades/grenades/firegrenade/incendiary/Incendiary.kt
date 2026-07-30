package club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary

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


const val INCENDIARY_RESOURCE_KEY = "incendiary"


object IncendiaryProperties : GrenadeProperties {
    override val entity: RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> =
        FireGrenadeRegistryHelper.incendiaryEntity
    override val item: RegistryObject<out CounterStrikeGrenadeItem> = FireGrenadeRegistryHelper.incendiaryItem
    override val resourceKey: String = INCENDIARY_RESOURCE_KEY
    override val sounds: GrenadeCommonSounds = IncendiarySounds
    override val damageTypes: GrenadeCommonDamageTypes = IncendiaryDamageTypes
}

object IncendiaryDamageTypes : GrenadeCommonDamageTypes {
    val fire = ModDamageTypes.registerSingle("incendiary/fire")
    override val hit = ModDamageTypes.registerSingle("incendiary/hit")
}

object IncendiarySounds : GrenadeCommonSounds {
    override val hitBlock = GrenadeSoundData.create("incendiary.hit_block")
    override val hitEntity: GrenadeSoundData = ModSoundEvents.HIT_ENTITY
    override val draw = GrenadeSoundData.create("incendiary.draw")
    override val pinPull = GrenadeSoundData.create("incendiary.pinpull")
    override val pinPullStart = GrenadeSoundData.create("incendiary.pinpull_start")
    override val `throw` = GrenadeSoundData.create("incendiary.throw")

    val detonate = GrenadeSoundData.create("incendiary.detonate")
    val pop = GrenadeSoundData.create("incendiary.pop")
    val detonateDistant = GrenadeSoundData.create("incendiary.detonate_distant")
    val detonateAir = GrenadeSoundData.create("incendiary.detonate_air")
}
