package club.pisquad.minecraft.csgrenades.client.sound.firegrenade

import club.pisquad.minecraft.csgrenades.client.sound.GrenadeSoundData

object IncendiarySoundEvents {
    val hitBlock = GrenadeSoundData.Companion.createHitBlock("incendiary.hit_block")
    val detonate = GrenadeSoundData.Companion.create("incendiary.detonate")
    val detonateDistant = GrenadeSoundData.Companion.create("incendiary.detonate_distant")
    val detonateAir = GrenadeSoundData.Companion.create("incendiary.detonate_air")
    val draw = GrenadeSoundData.Companion.createDraw("incendiary.draw")
    val pinpull = GrenadeSoundData.Companion.create("incendiary.pinpull")
    val pinpullStart = GrenadeSoundData.Companion.create("incendiary.pinpull_start")
    val pop = GrenadeSoundData.Companion.create("incendiary.pop")
    val throwSound = GrenadeSoundData.Companion.createThrow("incendiary.throw")
}