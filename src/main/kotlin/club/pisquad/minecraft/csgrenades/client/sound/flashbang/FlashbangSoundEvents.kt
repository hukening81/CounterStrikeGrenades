package club.pisquad.minecraft.csgrenades.client.sound.flashbang

import club.pisquad.minecraft.csgrenades.client.sound.GrenadeSoundData

object FlashbangSoundEvents {
    val draw = GrenadeSoundData.Companion.createDraw("flashbang.draw")
    val explode = GrenadeSoundData.Companion.create("flashbang.explode")
    val explodeDistant = GrenadeSoundData.Companion.create("flashbang.explode_distant")
    val ring = GrenadeSoundData.Companion.create("flashbang.ring")
    val ringLoop = GrenadeSoundData.Companion.create("flashbang.ring_loop")
    val hitBlock = GrenadeSoundData.Companion.createHitBlock("flashbang.hit_block")
    val throwSound = GrenadeSoundData.Companion.createThrow("flashbang.throw")
    val pinpull = GrenadeSoundData.Companion.create("flashbang.pinpull")
    val pinpullStart = GrenadeSoundData.Companion.create("flashbang.pinpull_start")
}