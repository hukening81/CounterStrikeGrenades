package club.pisquad.minecraft.csgrenades.registry.sounds

object MolotovSoundEvents {
    val detonate = GrenadeSoundData.create("molotov.detonate")
    val detonateDistant = GrenadeSoundData.create("molotov.detonate_distant")
    val detonateAir = GrenadeSoundData.create("molotov.detonate_air")
    val draw = GrenadeSoundData.createDraw("molotov.draw")
    val extinguish = GrenadeSoundData.create("molotov.extinguish")
    val fireIdle = GrenadeSoundData.create("molotov.fire_idle")
    val ignite = GrenadeSoundData.create("molotov.ignite")
    val fireLoop = GrenadeSoundData.create("molotov.fire_loop")
    val fireFadeout = GrenadeSoundData.create("molotov.fire_fadeout")
    val throwSound = GrenadeSoundData.createThrow("molotov.throw")
    val smash = GrenadeSoundData.create("molotov.smash")
    val hitBlock = GrenadeSoundData.createHitBlock("molotov.bounce")
}
