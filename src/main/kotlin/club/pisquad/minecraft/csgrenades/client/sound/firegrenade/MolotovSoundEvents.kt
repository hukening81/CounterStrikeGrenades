package club.pisquad.minecraft.csgrenades.client.sound.firegrenade

import club.pisquad.minecraft.csgrenades.client.sound.GrenadeSoundData

object MolotovSoundEvents {
    val detonate = GrenadeSoundData.Companion.create("molotov.detonate")
    val detonateDistant = GrenadeSoundData.Companion.create("molotov.detonate_distant")
    val detonateAir = GrenadeSoundData.Companion.create("molotov.detonate_air")
    val draw = GrenadeSoundData.Companion.createDraw("molotov.draw")
    val extinguish = GrenadeSoundData.Companion.create("molotov.extinguish")
    val fireIdle = GrenadeSoundData.Companion.create("molotov.fire_idle")
    val ignite = GrenadeSoundData.Companion.create("molotov.ignite")
    val fireLoop = GrenadeSoundData.Companion.create("molotov.fire_loop")
    val fireFadeout = GrenadeSoundData.Companion.create("molotov.fire_fadeout")
    val throwSound = GrenadeSoundData.Companion.createThrow("molotov.throw")
    val smash = GrenadeSoundData.Companion.create("molotov.smash")
    val hitBlock = GrenadeSoundData.Companion.createHitBlock("molotov.bounce")
}