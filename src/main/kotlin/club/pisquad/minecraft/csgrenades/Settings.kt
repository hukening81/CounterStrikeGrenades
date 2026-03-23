package club.pisquad.minecraft.csgrenades

const val GRENADE_ENTITY_SIZE = 0.25
const val GRENADE_ENTITY_SIZE_HALF = GRENADE_ENTITY_SIZE.div(2)

const val SERVER_MESSAGE_RANGE: Double = 100.0


const val BOUNCE_RESTORATION_RATE: Double = 0.7
const val BOUNCE_FRICTION: Double = 0.2
const val MINIMUM_VELOCITY_AFTER_BOUNCE = 0.1

const val AIR_DRAG_CONSTANT = 1.0
const val GRAVITY_CONSTANT = 0.05

const val POSITION_ERROR_TOLERANCE: Double = 1.0
const val SERVER_TRAJECTORY_NODE_CACHE_MAX_SIZE: Int = 40

// Common sound volumes
object CommonSoundVolumes {
    const val HIT_BLOCK: Double = 0.03
    const val THROW: Double = 1.0
    const val DRAW: Double = 1.0
}

