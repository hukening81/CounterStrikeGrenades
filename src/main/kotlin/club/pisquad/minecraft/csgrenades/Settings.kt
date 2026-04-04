package club.pisquad.minecraft.csgrenades

object ModSettings {
    const val SERVER_MESSAGE_RANGE: Double = 128.0

    // Within how much time after pressing jump key, a throw is considered a jump throw
    const val JUMP_THROW_TIME_WINDOW: Double = 0.5

    object Entity {
        const val GRENADE_ENTITY_SIZE = 0.25
        const val GRENADE_ENTITY_SIZE_HALF = GRENADE_ENTITY_SIZE.div(2.0)
        const val POSITION_ERROR_TOLERANCE: Double = 1.0
        const val SERVER_TRAJECTORY_NODE_CACHE_SIZE: Int = 20 // 1 second

        object Physics {
            const val BOUNCE_RESTORATION_RATE: Double = 0.7
            const val BOUNCE_FRICTION: Double = 0.2
            const val MINIMUM_VELOCITY_AFTER_BOUNCE = 0.1

            const val AIR_DRAG_CONSTANT = 1.0
            const val GRAVITY_CONSTANT = 0.05

            const val ROTATION_SPEED: Double = 1.0
        }
    }

    object Sound {
        const val EXPLOSION_SOUND_CHANGE_DISTANCE: Double = 32.0

        object Volume {
            const val HIT_BLOCK: Double = 10.0
            const val HIT_ENTITY: Double = 10.0
            const val THROW: Double = 10.0
            const val DRAW: Double = 10.0
        }
    }
}

object AnimationTiming {
    const val PIN_PULL_START = 0.58
    const val PIN_PULL = 0.1
}