package club.pisquad.minecraft.csgrenades.client.input

import club.pisquad.minecraft.csgrenades.GrenadeType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.entity.player.PlayerEvent

/**Throw action related events
 *
 * These events only fires on client side, they are used internally to play sound
 *
 * according to debundled sound files from cs2, throwing action has the following phases
 *
 * - pinpull_start: 0.58 second
 * - pinpull: 0.79 second
 *
 * Future animations should have the above durations
 */
abstract class InputEvent(player: Player, val grenadeType: GrenadeType) : PlayerEvent(player)


class PinPullStartEvent(player: Player, grenadeType: GrenadeType) : InputEvent(player, grenadeType)

class PinPullEvent(player: Player, grenadeType: GrenadeType) : InputEvent(player, grenadeType)

class ThrowEvent(
    player: Player,
    grenadeType: GrenadeType,
    val jumpThrow: Boolean,
    val origin: Vec3,
    val velocity: Vec3,
) : InputEvent(player, grenadeType)