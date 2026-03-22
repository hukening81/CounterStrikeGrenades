package club.pisquad.minecraft.csgrenades.grenades.hegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.client.sound.SimpleGrenadeSound
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
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
    override val draw = SimpleGrenadeSound.Companion.createDraw("hegrenade.draw")
    override val hitBlock = SimpleGrenadeSound.Companion.createHitBlock("hegrenade.hit_block")
    val explode = SimpleGrenadeSound.Companion.create("hegrenade.explode")
    val explodeDistant = SimpleGrenadeSound.Companion.create("hegrenade.explode_distant")
    override val `throw` = SimpleGrenadeSound.Companion.createThrow("hegrenade.throw")
    val pinpull = SimpleGrenadeSound.Companion.create("hegrenade.pinpull")
    val pinpullStart = SimpleGrenadeSound.Companion.create("hegrenade.pinpull_start")
}