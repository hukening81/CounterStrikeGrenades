package club.pisquad.minecraft.csgrenades.api

/**
 * This API should only be called on server side for consistant behavior
 */
object CSGrenadesAPI {
    val server = CSGrenadeServerAPI
    val client = CSGrenadeClientAPI
}
