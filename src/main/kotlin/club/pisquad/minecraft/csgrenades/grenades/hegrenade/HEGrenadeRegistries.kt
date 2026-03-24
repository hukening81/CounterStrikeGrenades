package club.pisquad.minecraft.csgrenades.grenades.hegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.core.sound.DistanceSegmentedSoundData
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import net.minecraft.world.item.Item

object HEGrenadeRegistries :
    CounterStrikeGrenadeRegistries<HEGrenadeEntity, HEGrenadeItem, HEGrenadeDamageTypes, HEGrenadeSoundEvents>(
        GrenadeType.HE_GRENADE,
        HEGrenadeDamageTypes,
        HEGrenadeSoundEvents,
        ::HEGrenadeEntity,
        { HEGrenadeItem(Item.Properties()) }
    ) {
}

object HEGrenadeDamageTypes : GrenadeEntityDamageTypes {
    val explosion = ModDamageTypes.registerSingle("hegrenade.explosion")
    override val hit = ModDamageTypes.registerSingle("hegrenade.hit")
    override val main = explosion

}

object HEGrenadeSoundEvents : GrenadeSoundEvents {
    override val draw = GrenadeSoundData.Companion.createDraw("hegrenade.draw")
    override val hitBlock = GrenadeSoundData.Companion.createHitBlock("hegrenade.hit_block")
    val explode = DistanceSegmentedSoundData.createTwoPhasedExplosion(
        GrenadeSoundData.create("hegrenade.explode"),
        GrenadeSoundData.create("hegrenade.explode_distant")
    )

    //    val explode = GrenadeSoundData.Companion.create("hegrenade.explode")
//    val explodeDistant = GrenadeSoundData.Companion.create("hegrenade.explode_distant")
    override val `throw` = GrenadeSoundData.Companion.createThrow("hegrenade.throw")
    val pinpull = GrenadeSoundData.Companion.create("hegrenade.pinpull")
    val pinpullStart = GrenadeSoundData.Companion.create("hegrenade.pinpull_start")
}