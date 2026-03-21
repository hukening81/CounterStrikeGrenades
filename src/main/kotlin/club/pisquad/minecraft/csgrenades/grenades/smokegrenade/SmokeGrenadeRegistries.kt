package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.client.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import net.minecraft.world.item.Item

object SmokeGrenadeRegistries :
    CounterStrikeGrenadeRegistries<SmokeGrenadeEntity, SmokeGrenadeItem, SmokeGrenadeDamageTypes, SmokeGrenadeSoundEvents>(
        GrenadeType.SMOKE_GRENADE,
        SmokeGrenadeDamageTypes,
        SmokeGrenadeSoundEvents,
        ::SmokeGrenadeEntity,
        { SmokeGrenadeItem(Item.Properties()) }
    ) {
}

object SmokeGrenadeDamageTypes : GrenadeEntityDamageTypes {
    override val hit = ModDamageTypes.registerSingle("smokegrenade.hit")
    override val main = hit
}

object SmokeGrenadeSoundEvents : GrenadeSoundEvents {
    val can = GrenadeSoundData.Companion.create("smokegrenade.can")
    override val draw = GrenadeSoundData.Companion.createDraw("smokegrenade.draw")
    override val hitBlock = GrenadeSoundData.Companion.createHitBlock("smokegrenade.hit")
    override val `throw` = GrenadeSoundData.Companion.createThrow("smokegrenade.throw")
    val pinpull = GrenadeSoundData.Companion.create("smokegrenade.pinpull")
    val pinpullStart = GrenadeSoundData.Companion.create("smokegrenade.pinpull_start")
    val explodeDistant = GrenadeSoundData.Companion.create("smokegrenade.explode_distant")
    val clear = GrenadeSoundData.Companion.create("smokegrenade.clear")
    val emit = GrenadeSoundData.Companion.create("smokegrenade.emit")
}