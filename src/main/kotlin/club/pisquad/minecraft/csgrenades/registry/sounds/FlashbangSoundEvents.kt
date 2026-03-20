package club.pisquad.minecraft.csgrenades.registry.sounds

object FlashbangSoundEvents {
    val draw = GrenadeSoundData.createDraw("flashbang.draw")
    val explode = GrenadeSoundData.create("flashbang.explode")
    val explodeDistant = GrenadeSoundData.create("flashbang.explode_distant")
    val ring = GrenadeSoundData.create("flashbang.ring")
    val ringLoop = GrenadeSoundData.create("flashbang.ring_loop")
    val hitBlock = GrenadeSoundData.createHitBlock("flashbang.hit_block")
    val throwSound = GrenadeSoundData.createThrow("flashbang.throw")
    val pinpull = GrenadeSoundData.create("flashbang.pinpull")
    val pinpullStart = GrenadeSoundData.create("flashbang.pinpull_start")
}
