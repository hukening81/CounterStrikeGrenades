package club.pisquad.minecraft.csgrenades.client.sound.smokegrenade

import club.pisquad.minecraft.csgrenades.client.sound.GrenadeSoundData

object SmokeGrenadeSoundEvents {
    val can = GrenadeSoundData.Companion.create("smokegrenade.can")
    val draw = GrenadeSoundData.Companion.createDraw("smokegrenade.draw")
    val hitBlock = GrenadeSoundData.Companion.createHitBlock("smokegrenade.hit")
    val throwSound = GrenadeSoundData.Companion.createThrow("smokegrenade.throw")
    val pinpull = GrenadeSoundData.Companion.create("smokegrenade.pinpull")
    val pinpullStart = GrenadeSoundData.Companion.create("smokegrenade.pinpull_start")
    val explodeDistant = GrenadeSoundData.Companion.create("smokegrenade.explode_distant")
    val clear = GrenadeSoundData.Companion.create("smokegrenade.clear")
    val emit = GrenadeSoundData.Companion.create("smokegrenade.emit")
}