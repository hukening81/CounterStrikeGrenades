package club.pisquad.minecraft.csgrenades.client.sound.hegrenade

import club.pisquad.minecraft.csgrenades.client.sound.GrenadeSoundData

object HEGrenadeSoundEvents {
    val draw = GrenadeSoundData.Companion.createDraw("hegrenade.draw")
    val explode = GrenadeSoundData.Companion.create("hegrenade.explode")
    val explodeDistant = GrenadeSoundData.Companion.create("hegrenade.explode_distant")
    val throwSound = GrenadeSoundData.Companion.createThrow("hegrenade.throw")
    val hitBlock = GrenadeSoundData.Companion.createHitBlock("hegrenade.hit_block")
    val pinpull = GrenadeSoundData.Companion.create("hegrenade.pinpull")
    val pinpullStart = GrenadeSoundData.Companion.create("hegrenade.pinpull_start")
}