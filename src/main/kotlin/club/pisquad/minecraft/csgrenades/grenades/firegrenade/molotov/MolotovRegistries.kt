package club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.client.sound.GrenadeSoundData
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
    val detonate = GrenadeSoundData.Companion.create("molotov.detonate")
    val detonateDistant = GrenadeSoundData.Companion.create("molotov.detonate_distant")
    val detonateAir = GrenadeSoundData.Companion.create("molotov.detonate_air")
    override val draw = GrenadeSoundData.Companion.createDraw("molotov.draw")
    val extinguish = GrenadeSoundData.Companion.create("molotov.extinguish")
    val fireIdle = GrenadeSoundData.Companion.create("molotov.fire_idle")
    val ignite = GrenadeSoundData.Companion.create("molotov.ignite")
    val fireLoop = GrenadeSoundData.Companion.create("molotov.fire_loop")
    val fireFadeout = GrenadeSoundData.Companion.create("molotov.fire_fadeout")
    override val `throw` = GrenadeSoundData.Companion.createThrow("molotov.throw")
    val smash = GrenadeSoundData.Companion.create("molotov.smash")
    override val hitBlock = GrenadeSoundData.Companion.createHitBlock("molotov.bounce")
}