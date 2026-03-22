package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.client.sound.SimpleGrenadeSound
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
    val can = SimpleGrenadeSound.Companion.create("smokegrenade.can")
    override val draw = SimpleGrenadeSound.Companion.createDraw("smokegrenade.draw")
    override val hitBlock = SimpleGrenadeSound.Companion.createHitBlock("smokegrenade.hit")
    override val `throw` = SimpleGrenadeSound.Companion.createThrow("smokegrenade.throw")
    val pinpull = SimpleGrenadeSound.Companion.create("smokegrenade.pinpull")
    val pinpullStart = SimpleGrenadeSound.Companion.create("smokegrenade.pinpull_start")
    val explodeDistant = SimpleGrenadeSound.Companion.create("smokegrenade.explode_distant")
    val clear = SimpleGrenadeSound.Companion.create("smokegrenade.clear")
    val emit = SimpleGrenadeSound.Companion.create("smokegrenade.emit")
}