package club.pisquad.minecraft.csgrenades.grenades.flashbang

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.client.sound.SimpleGrenadeSound
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.registry.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.registry.GrenadeSoundEvents
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import net.minecraft.world.item.Item

object FlashbangRegistries :
    CounterStrikeGrenadeRegistries<FlashBangEntity, FlashBangItem, FlashBangDamageTypes, FlashbangSoundEvents>(
        GrenadeType.FLASH_BANG,
        FlashBangDamageTypes,
        FlashbangSoundEvents,
        ::FlashBangEntity,
        { FlashBangItem(Item.Properties()) }
    )

object FlashBangDamageTypes : GrenadeEntityDamageTypes {
    override val hit = ModDamageTypes.registerSingle("flashbang.hit")
    override val main = hit
}

object FlashbangSoundEvents : GrenadeSoundEvents {
    override val draw = SimpleGrenadeSound.Companion.createDraw("flashbang.draw")
    val explode = SimpleGrenadeSound.Companion.create("flashbang.explode")
    val explodeDistant = SimpleGrenadeSound.Companion.create("flashbang.explode_distant")
    val ring = SimpleGrenadeSound.Companion.create("flashbang.ring")
    val ringLoop = SimpleGrenadeSound.Companion.create("flashbang.ring_loop")
    override val hitBlock = SimpleGrenadeSound.Companion.createHitBlock("flashbang.hit_block")
    override val `throw` = SimpleGrenadeSound.Companion.createThrow("flashbang.throw")
    val pinpull = SimpleGrenadeSound.Companion.create("flashbang.pinpull")
    val pinpullStart = SimpleGrenadeSound.Companion.create("flashbang.pinpull_start")
}