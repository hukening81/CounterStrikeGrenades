package club.pisquad.minecraft.csgrenades.grenades.flashbang

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.SERVER_MESSAGE_RANGE
import club.pisquad.minecraft.csgrenades.core.CounterStrikeGrenadeRegistries
import club.pisquad.minecraft.csgrenades.core.sound.DistanceSegmentedSoundData
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
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
    override val draw = GrenadeSoundData.Companion.create("flashbang.draw")

    val explode = DistanceSegmentedSoundData(
        Pair(
            25.0, GrenadeSoundData.create("flashbang.explode"),

            ),
        Pair(
            SERVER_MESSAGE_RANGE,
            GrenadeSoundData.create("flashbang.explode_distant"),
        )
    )

    //    val explode = GrenadeSoundData.Companion.create("flashbang.explode")
//    val explodeDistant = GrenadeSoundData.Companion.create("flashbang.explode_distant")
    val ring = GrenadeSoundData.Companion.create("flashbang.ring")
    val ringLoop = GrenadeSoundData.Companion.create("flashbang.ring_loop")
    override val hitBlock = GrenadeSoundData.Companion.create("flashbang.hit_block")
    override val `throw` = GrenadeSoundData.Companion.create("flashbang.throw")
    val pinpull = GrenadeSoundData.Companion.create("flashbang.pinpull")
    val pinpullStart = GrenadeSoundData.Companion.create("flashbang.pinpull_start")
}