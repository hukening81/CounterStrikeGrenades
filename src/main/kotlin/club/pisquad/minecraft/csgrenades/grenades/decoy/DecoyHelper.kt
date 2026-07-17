package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.TICKS_PER_SECOND
import club.pisquad.minecraft.csgrenades.epsilon
import club.pisquad.minecraft.csgrenades.physics.GrenadeDuration
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.round
import kotlin.random.Random

//
//sealed interface DecoySoundPlayer {
//    fun play(tick: Int)
//}
//
//@Serializable
//sealed interface DecoyFakeSoundData {
//    fun getAudioPlayer(): DecoySoundPlayer
//
//    @Serializable
//    class Vanilla(val name: String, val pattern: DecoySoundPattern) : DecoyFakeSoundData {
//        override fun getAudioPlayer(): DecoySoundPlayer {
//            TODO("Not yet implemented")
//        }
//    }
//
//    @Serializable
//    class Tacz(val name: String, val pattern: DecoySoundPattern) : DecoyFakeSoundData {
//        override fun getAudioPlayer(): DecoySoundPlayer {
//            TODO("Not yet implemented")
//        }
//    }
//}
@Serializable
class DecoyFirePattern(
    val interval: Double,
    val groupList: List<GroupEntry>
) {
    @Serializable
    class GroupEntry(val groupSize: Int, val delay: Int)
//
//    private class FirePatternGenerator(
//        val duration: Double,
//        val interval: Double,
//        val minGroupInterval: Double,
//        val maxGroupInterval: Double,
//        val ammoCount: Int = Int.MAX_VALUE,
//        val reloadTime: Double = 0.0,
//        val startDelay: Double = 0.0
//    ) {
//        var currentGroup = 0
//        var ammoUsed: Int = 0
//        var currentDuration = 0.0
//
//        fun nextGroup(): GroupEntry? {
//            if (currentDuration > duration) {
//                return null
//            }
//
//            val groupDelay = (if (currentGroup == 0) {
//                startDelay
//            } else if (ammoUsed >= ammoCount) {
//                Random.nextDouble(minGroupInterval, maxGroupInterval) + reloadTime
//            } else {
//                Random.nextDouble(minGroupInterval, maxGroupInterval)
//            }).toTick().toInt()
//
//        }

//        private fun createGroupPattern(): GroupEntry {}

    companion object {
        fun createFireModeAuto(
            duration: Double,
            interval: Double,
            groupSize: IntRange,
            groupInterval: IntRange,
        ): DecoyFirePattern {
            TODO()
        }

        fun createFireModeSemi(
            duration: Double,
            interval: Double,
            groupSize: IntRange,
            groupInterval: IntRange
        ): DecoyFirePattern {
            return createFireModeAuto(duration, interval, groupSize, groupInterval)
        }

        fun createFireModeBurst(
            duration: Double,
            bulletCount: Int,
            interval: Double,
            burstInterval: Double
        ): DecoyFirePattern {
            TODO()
        }

        fun create(
            duration: Double,
            interval: Double,
            groupSize: IntRange,
            groupInterval: IntRange,
        ): DecoyFirePattern {
            var currentTime = 0.0

            val groupList = mutableListOf<GroupEntry>()

            // The first group always have zero delay
            val size = groupSize.random()
            groupList.add(GroupEntry(size, 0))
            currentTime += size * interval

            while (currentTime < duration) {
                val size = groupSize.random()
                val delay = groupInterval.random()

                groupList.add(GroupEntry(size, delay))

                currentTime += (size * interval + GrenadeDuration.fromTick(delay.toDouble()).seconds)
            }
            return DecoyFirePattern(interval, groupList)
        }
    }
}

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
            println(ammoRemain)

            if (ammoRemain <= 0) {
                groupInterval += reloadTime
                println("Resetting ammo count")
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
            currentTime += (t + groupInterval)
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