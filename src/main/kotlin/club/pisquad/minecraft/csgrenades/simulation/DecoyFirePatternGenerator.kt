package club.pisquad.minecraft.csgrenades.simulation

import club.pisquad.minecraft.csgrenades.entity.DecoyGrenadeEntity
import com.tacz.guns.api.item.gun.FireMode
import kotlin.random.Random

object DecoyFirePatternGenerator {

    private const val TOTAL_DURATION_TICKS = 15 * 20
    private const val MIN_BURST_DELAY_TICKS = 25
    private const val MAX_BURST_DELAY_TICKS = 60
    private const val MIN_SHOTS_IN_BURST = 2
    private const val MAX_SHOTS_IN_BURST = 5

    fun generateFireTimestamps(entity: DecoyGrenadeEntity): List<Int> {
        val gunId = entity.entityData.get(DecoyGrenadeEntity.GUN_ID_TO_PLAY_ACCESSOR)
        if (gunId.isBlank()) {
            return generateFallbackTimestamps()
        }

        val fireModeName = entity.entityData.get(DecoyGrenadeEntity.GUN_FIRE_MODE_ACCESSOR)
        val fireMode = try {
            FireMode.valueOf(fireModeName)
        } catch (e: Exception) {
            FireMode.SEMI
        }
        val rpm = entity.entityData.get(DecoyGrenadeEntity.GUN_RPM_ACCESSOR)
        val shootIntervalMs = entity.entityData.get(DecoyGrenadeEntity.GUN_SHOOT_INTERVAL_MS_ACCESSOR)

        return when (fireMode) {
            FireMode.AUTO -> generateAutoTimestamps(rpm, shootIntervalMs)
            else -> generateSemiTimestamps()
        }
    }

    private fun generateAutoTimestamps(rpm: Int, shootIntervalMs: Int): List<Int> {
        if (rpm <= 0 || shootIntervalMs <= 0) return generateSemiTimestamps()

        val timestamps = mutableListOf<Int>()
        var currentTime = 0
        val shootIntervalTicks = (shootIntervalMs / 50.0).coerceAtLeast(1.0)

        // First burst with shorter delay
        currentTime += Random.nextInt(MIN_BURST_DELAY_TICKS / 2, MAX_BURST_DELAY_TICKS / 2)

        while (currentTime < TOTAL_DURATION_TICKS) {
            val shotsInBurst = Random.nextInt(MIN_SHOTS_IN_BURST, MAX_SHOTS_IN_BURST + 1)
            for (i in 0 until shotsInBurst) {
                val shotTime = currentTime + (i * shootIntervalTicks).toInt()
                if (shotTime < TOTAL_DURATION_TICKS) {
                    timestamps.add(shotTime)
                } else {
                    break
                }
            }
            currentTime = timestamps.lastOrNull() ?: currentTime
            currentTime += Random.nextInt(MIN_BURST_DELAY_TICKS, MAX_BURST_DELAY_TICKS + 1)
        }
        return timestamps.sorted()
    }

    private fun generateSemiTimestamps(): List<Int> {
        val timestamps = mutableListOf<Int>()
        var currentTime = 0

        // First shot with shorter delay
        currentTime += Random.nextInt(MIN_BURST_DELAY_TICKS / 2, MAX_BURST_DELAY_TICKS / 2)
        timestamps.add(currentTime)

        while (currentTime < TOTAL_DURATION_TICKS) {
            currentTime += Random.nextInt(MIN_BURST_DELAY_TICKS, MAX_BURST_DELAY_TICKS + 1)
            if (currentTime < TOTAL_DURATION_TICKS) {
                timestamps.add(currentTime)
            }
        }
        return timestamps.sorted()
    }

    private fun generateFallbackTimestamps(): List<Int> {
        return generateSemiTimestamps() // Fallback behaves like SEMI
    }
}
