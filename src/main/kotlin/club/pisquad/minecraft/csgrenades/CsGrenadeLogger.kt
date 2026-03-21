package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object ModLogger {
    val logger: Logger = LogManager.getLogger(CounterStrikeGrenades.ID)

    // Basic passthrough (optional, just for clarity)
    fun info(msg: String, vararg args: Any?) = logger.info(msg, *args)
    fun debug(msg: String, vararg args: Any?) = logger.debug(msg, *args)
    fun warn(msg: String, vararg args: Any?) = logger.warn(msg, *args)
    fun error(msg: String, vararg args: Any?) = logger.error(msg, *args)
    fun trace(msg: String, vararg args: Any?) = logger.trace(msg, *args)

    fun trace(entity: CounterStrikeGrenadeEntity, msg: String, vararg args: Any?) {
        // NOTE(hukening81): not sure how "msg.format(args)" affects performance
        // NOTE(hukening81): someone need to write a generic method for following functions using logger.log
        logger.trace("[{}(id:{} tick:{})] {}", entity.grenadeType, entity.id, entity.tickCount, msg.format(args))
    }

    fun debug(entity: CounterStrikeGrenadeEntity, msg: String, vararg args: Any?) {
        logger.debug("[{}(id:{} tick:{})] {}", entity.grenadeType, entity.id, entity.tickCount, msg.format(args))
    }

    fun info(entity: CounterStrikeGrenadeEntity, msg: String, vararg args: Any?) {
        logger.info("[{}(id:{} tick:{})] {}", entity.grenadeType, entity.id, entity.tickCount, msg.format(args))
    }
}
