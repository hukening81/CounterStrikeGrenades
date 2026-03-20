package club.pisquad.minecraft.csgrenades.client.sound.decoy

import club.pisquad.minecraft.csgrenades.client.sound.GrenadeSoundData

object DecoySoundEvents {
    val draw = GrenadeSoundData.Companion.createDraw("decoy.draw")
    val throwSound = GrenadeSoundData.Companion.createThrow("decoy.throw")
    val pinpull = GrenadeSoundData.Companion.create("decoy.pinpull")
    val pinpullStart = GrenadeSoundData.Companion.create("decoy.pinpull_start")
}