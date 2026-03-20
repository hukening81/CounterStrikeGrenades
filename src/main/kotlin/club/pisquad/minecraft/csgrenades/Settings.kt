package club.pisquad.minecraft.csgrenades

const val GRENADE_ENTITY_SIZE = 0.25
const val GRENADE_ENTITY_SIZE_HALF = GRENADE_ENTITY_SIZE.div(2)

// Incendiary
const val INCENDIARY_PARTICLE_DENSITY = 1
const val INCENDIARY_PARTICLE_LIFETIME = 1200

const val BOUNCE_RESTORATION_RATE: Double = 0.7
const val BOUNCE_FRICTION: Double = 0.2
const val MINIMUM_VELOCITY_AFTER_BOUNCE = 0.1

const val AIR_DRAG_CONSTANT = 1.0
const val GRAVITY_CONSTANT = 0.05

const val POSITION_ERROR_TOLERANCE: Double = 1.0
const val SERVER_TRAJECTORY_NODE_CACHE_MAX_SIZE: Int = 40

// Sound Volume
const val BLOCK_BOUNCE_SOUND_VOLUME: Float = 0.5f