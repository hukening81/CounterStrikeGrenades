package club.pisquad.minecraft.csgrenades.simulation

import club.pisquad.minecraft.csgrenades.entity.DecoyGrenadeEntity
import club.pisquad.minecraft.csgrenades.entity.decoy.DecoyGunData
import com.tacz.guns.api.item.gun.FireMode
import kotlin.random.Random

object DecoyFirePatternGenerator {

    private const val TOTAL_DURATION_TICKS = 15 * 20
    private const val MIN_BURST_DELAY_TICKS = 25
    private const val MAX_BURST_DELAY_TICKS = 60
    private const val MIN_SHOTS_IN_BURST = 2
    private const val MAX_SHOTS_IN_BURST = 5

    fun generateFireTimestamps(entity: DecoyGrenadeEntity): List<Int> {
        val gunData = entity.getGunData()
        if (gunData == null || !gunData.isValid) {
            return generateFallbackTimestamps()
        }

        return when (gunData.fireMode) {
            FireMode.AUTO -> generateAutoTimestamps(gunData)
            else -> generateSemiTimestamps()
        }
    }

    private fun generateAutoTimestamps(gunData: DecoyGunData): List<Int> {
        val timestamps = mutableListOf<Int>()
        var currentTime = 0
        val shootIntervalTicks = (gunData.shootIntervalMs / 50.0).coerceAtLeast(1.0)

        currentTime += getRandomBurstDelay(isFirstBurst = true)

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
            currentTime += getRandomBurstDelay(isFirstBurst = false)
        }
        return timestamps
    }

    private fun generateSemiTimestamps(): List<Int> {
        val timestamps = mutableListOf<Int>()
        var currentTime = 0

        currentTime += getRandomBurstDelay(isFirstBurst = true)
        timestamps.add(currentTime)

        while (currentTime < TOTAL_DURATION_TICKS) {
            currentTime += getRandomBurstDelay(isFirstBurst = false)
            if (currentTime < TOTAL_DURATION_TICKS) {
                timestamps.add(currentTime)
            }
        }
        return timestamps
    }

    private fun generateFallbackTimestamps(): List<Int> {
        return generateSemiTimestamps()
    }

    private fun getRandomBurstDelay(isFirstBurst: Boolean): Int {
        val minDelay = if (isFirstBurst) MIN_BURST_DELAY_TICKS / 2 else MIN_BURST_DELAY_TICKS
        val maxDelay = if (isFirstBurst) MAX_BURST_DELAY_TICKS / 2 else MAX_BURST_DELAY_TICKS
        return Random.nextInt(minDelay, maxDelay + 1)
    }
}
