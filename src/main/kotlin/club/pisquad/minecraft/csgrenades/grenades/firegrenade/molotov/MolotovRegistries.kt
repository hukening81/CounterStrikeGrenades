package club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.client.sound.SimpleGrenadeSound
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import net.minecraft.world.item.Item

object MolotovRegistries :
    CounterStrikeGrenadeRegistries<MolotovEntity, MolotovItem, MolotovDamageTypes, MolotovSoundEvents>(
        GrenadeType.MOLOTOV,
        MolotovDamageTypes,
        MolotovSoundEvents,
        ::MolotovEntity,
        { MolotovItem(Item.Properties()) }
    )

object MolotovDamageTypes : GrenadeEntityDamageTypes {

    val fire = ModDamageTypes.registerSingle("molotov.fire")
    override val hit = ModDamageTypes.registerSingle("molotov.hit")
    override val main = fire
}

object MolotovSoundEvents : GrenadeSoundEvents {
    val detonate = SimpleGrenadeSound.Companion.create("molotov.detonate")
    val detonateDistant = SimpleGrenadeSound.Companion.create("molotov.detonate_distant")
    val detonateAir = SimpleGrenadeSound.Companion.create("molotov.detonate_air")
    override val draw = SimpleGrenadeSound.Companion.createDraw("molotov.draw")
    val extinguish = SimpleGrenadeSound.Companion.create("molotov.extinguish")
    val fireIdle = SimpleGrenadeSound.Companion.create("molotov.fire_idle")
    val ignite = SimpleGrenadeSound.Companion.create("molotov.ignite")
    val fireLoop = SimpleGrenadeSound.Companion.create("molotov.fire_loop")
    val fireFadeout = SimpleGrenadeSound.Companion.create("molotov.fire_fadeout")
    override val `throw` = SimpleGrenadeSound.Companion.createThrow("molotov.throw")
    val smash = SimpleGrenadeSound.Companion.create("molotov.smash")
    override val hitBlock = SimpleGrenadeSound.Companion.createHitBlock("molotov.bounce")
}