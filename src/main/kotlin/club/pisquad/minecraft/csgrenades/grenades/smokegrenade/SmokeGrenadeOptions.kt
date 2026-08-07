package club.pisquad.minecraft.csgrenades.grenades.smokegrenade


enum class VoxelDebugMode {
    NONE,
    ALL,
    EDGES,
    SPECIALS,
}

object SmokeGrenadeOptions {
    var voxelDebugMode: VoxelDebugMode = VoxelDebugMode.NONE
}