package club.pisquad.minecraft.csgrenades.registry.sounds

object IncendiarySoundEvents {
    val hitBlock = GrenadeSoundData.createHitBlock("incendiary.hit_block")
    val detonate = GrenadeSoundData.create("incendiary.detonate")
    val detonateDistant = GrenadeSoundData.create("incendiary.detonate_distant")
    val detonateAir = GrenadeSoundData.create("incendiary.detonate_air")
    val draw = GrenadeSoundData.createDraw("incendiary.draw")
    val pinpull = GrenadeSoundData.create("incendiary.pinpull")
    val pinpullStart = GrenadeSoundData.create("incendiary.pinpull_start")
    val pop = GrenadeSoundData.create("incendiary.pop")
    val throwSound = GrenadeSoundData.createThrow("incendiary.throw")
}
