package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.client.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangRegistries
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import net.minecraft.world.item.Item

object DecoyRegistries :
    CounterStrikeGrenadeRegistries<DecoyGrenadeEntity, DecoyGrenadeItem, DecoyDamageTypes, DecoySoundEvents>(
        GrenadeType.DECOY,
        DecoyDamageTypes,
        DecoySoundEvents,
        ::DecoyGrenadeEntity,
        { DecoyGrenadeItem(Item.Properties()) }) {
}

object DecoyDamageTypes : GrenadeEntityDamageTypes {
    val explosion = ModDamageTypes.registerSingle("decoy.explosion")
    override val hit = ModDamageTypes.registerSingle("decoy.hit")
    override val main = explosion


}

object DecoySoundEvents : GrenadeSoundEvents {
    override val draw = GrenadeSoundData.Companion.createDraw("decoy.draw")
    override val `throw` = GrenadeSoundData.Companion.createThrow("decoy.throw")
    override val hitBlock = FlashbangRegistries.sounds.hitBlock
    val pinpull = GrenadeSoundData.Companion.create("decoy.pinpull")
    val pinpullStart = GrenadeSoundData.Companion.create("decoy.pinpull_start")
}