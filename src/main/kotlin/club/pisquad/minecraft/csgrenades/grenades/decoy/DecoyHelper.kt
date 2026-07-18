package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.TICKS_PER_SECOND
import club.pisquad.minecraft.csgrenades.epsilon
import club.pisquad.minecraft.csgrenades.physics.GrenadeDuration
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.round
import kotlin.random.Random

internal object DecoyHelper {
    fun generateFirePattern(
        duration: Double,
        interval: Double,
        minGroupInterval: Double,
        maxGroupInterval: Double,
        groupSizeRange: IntRange,
        reloadTime: Double = 0.0,
        startDelay: Double = 0.0,
        ammoCount: Int = Int.MAX_VALUE,
    ): List<Int> {
        val result = mutableListOf(GrenadeDuration.convertSecondToWholeTick(startDelay))

        var currentTime = startDelay
        var ammoRemain = ammoCount

        while (currentTime < duration) {
            var groupInterval =
                if ((maxGroupInterval - minGroupInterval).absoluteValue < Double.epsilon()) {
                    minGroupInterval
                } else {
                    Random.nextDouble(
                        minGroupInterval,
                        maxGroupInterval
                    )
                }
            val groupSize = min(groupSizeRange.random(), ammoRemain)
            ammoRemain -= groupSize

            if (ammoRemain <= 0) {
                groupInterval += reloadTime
                ammoRemain = ammoCount
            }
            val group = mutableListOf<Int>()
            var t = 0.0
            var groupTime = 0
            repeat(groupSize) {
                t += interval
                val a = GrenadeDuration.convertSecondToWholeTick(t)
                group.add(a - groupTime)
                groupTime = a
            }
            group.removeLast()
            group.add(GrenadeDuration.convertSecondToWholeTick(groupInterval))
            currentTime += (t - interval + groupInterval)
            result.addAll(group)
        }
        return result
    }

    fun rpmToTickDelay(rpm: Int): Int {
        return round(TICKS_PER_SECOND * 60 / rpm).toInt()
    }

    fun rpmToSecondDelay(rpm: Int): Double {
        return 60.0 / rpm
    }
}