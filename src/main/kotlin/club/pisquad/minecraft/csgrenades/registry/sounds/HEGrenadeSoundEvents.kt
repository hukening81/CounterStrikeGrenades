package club.pisquad.minecraft.csgrenades.registry.sounds

object HEGrenadeSoundEvents {
    val draw = GrenadeSoundData.createDraw("hegrenade.draw")
    val explode = GrenadeSoundData.create("hegrenade.explode")
    val explodeDistant = GrenadeSoundData.create("hegrenade.explode_distant")
    val throwSound = GrenadeSoundData.createThrow("hegrenade.throw")
    val hitBlock = GrenadeSoundData.createHitBlock("hegrenade.hit_block")
    val pinpull = GrenadeSoundData.create("hegrenade.pinpull")
    val pinpullStart = GrenadeSoundData.create("hegrenade.pinpull_start")
}
