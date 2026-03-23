package club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import net.minecraft.world.item.Item


object IncendiaryRegistries :
    CounterStrikeGrenadeRegistries<IncendiaryEntity, IncendiaryItem, IncendiaryDamageTypes, IncendiarySoundEvents>(
        GrenadeType.INCENDIARY,
        IncendiaryDamageTypes,
        IncendiarySoundEvents,
        ::IncendiaryEntity,
        { IncendiaryItem(Item.Properties()) }) {
}

object IncendiaryDamageTypes : GrenadeEntityDamageTypes {
    val fire = ModDamageTypes.registerSingle("incendiary.fire")
    override val hit = ModDamageTypes.registerSingle("incendiary.hit")
    override val main = fire
}

object IncendiarySoundEvents : GrenadeSoundEvents {
    override val hitBlock = GrenadeSoundData.Companion.createHitBlock("incendiary.hit_block")
    val detonate = GrenadeSoundData.Companion.create("incendiary.detonate")
    val detonateDistant = GrenadeSoundData.Companion.create("incendiary.detonate_distant")
    val detonateAir = GrenadeSoundData.Companion.create("incendiary.detonate_air")
    override val draw = GrenadeSoundData.Companion.createDraw("incendiary.draw")
    val pinpull = GrenadeSoundData.Companion.create("incendiary.pinpull")
    val pinpullStart = GrenadeSoundData.Companion.create("incendiary.pinpull_start")
    val pop = GrenadeSoundData.Companion.create("incendiary.pop")
    override val `throw` = GrenadeSoundData.Companion.createThrow("incendiary.throw")
}