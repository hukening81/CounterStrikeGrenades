package club.pisquad.minecraft.csgrenades.simulation

import club.pisquad.minecraft.csgrenades.constants.DecoyConstants
import club.pisquad.minecraft.csgrenades.entity.DecoyGrenadeEntity
import club.pisquad.minecraft.csgrenades.entity.decoy.DecoyGunData
import com.tacz.guns.api.item.gun.FireMode
import kotlin.random.Random

/**
 * 诱饵弹射击模式生成器
 * 核心功能：根据枪械类型生成逼真的射击时间戳列表
 */
object DecoyFirePatternGenerator {

    /**
     * 生成射击时间戳列表
     * 核心逻辑：根据枪械类型选择不同的射击模式
     */
    fun generateFireTimestamps(entity: DecoyGrenadeEntity): List<Int> {
        // 获取枪械数据
        val gunData = entity.getGunData()
        // 无枪械数据时使用fallback模式
        if (gunData == null || !gunData.isValid) {
            return generateFallbackTimestamps()
        }

        // 根据射击模式生成时间戳
        return when (gunData.fireMode) {
            FireMode.AUTO -> generateAutoTimestamps(gunData)

            // 自动模式
            else -> generateSemiTimestamps() // 半自动模式
        }
    }

    /**
     * 生成自动模式射击时间戳
     * 核心机制：模拟连发模式，按照枪械射速生成爆发式射击
     */
    private fun generateAutoTimestamps(gunData: DecoyGunData): List<Int> {
        val timestamps = mutableListOf<Int>()
        var currentTime = 0
        // 计算射击间隔（毫秒转ticks）
        val shootIntervalTicks = (gunData.shootIntervalMs / 50.0).coerceAtLeast(1.0)

        // 第一次爆发使用较短延迟
        currentTime += getRandomBurstDelay(isFirstBurst = true)

        // 生成射击时间戳
        while (currentTime < DecoyConstants.TOTAL_DURATION_TICKS) {
            // 随机生成爆发中的射击次数
            val shotsInBurst = Random.nextInt(DecoyConstants.MIN_SHOTS_IN_BURST, DecoyConstants.MAX_SHOTS_IN_BURST + 1)
            // 生成爆发内的射击时间戳
            for (i in 0 until shotsInBurst) {
                val shotTime = currentTime + (i * shootIntervalTicks).toInt()
                if (shotTime < DecoyConstants.TOTAL_DURATION_TICKS) {
                    timestamps.add(shotTime)
                } else {
                    break
                }
            }
            // 更新当前时间到最后一次射击
            currentTime = timestamps.lastOrNull() ?: currentTime
            // 添加爆发间延迟
            currentTime += getRandomBurstDelay(isFirstBurst = false)
        }
        return timestamps
    }

    /**
     * 生成半自动模式射击时间戳
     * 核心机制：模拟单点模式，每次射击间隔较长
     */
    private fun generateSemiTimestamps(): List<Int> {
        val timestamps = mutableListOf<Int>()
        var currentTime = 0

        // 第一次射击使用较短延迟
        currentTime += getRandomBurstDelay(isFirstBurst = true)
        timestamps.add(currentTime)

        // 生成后续射击时间戳
        while (currentTime < DecoyConstants.TOTAL_DURATION_TICKS) {
            // 添加射击间隔
            currentTime += getRandomBurstDelay(isFirstBurst = false)
            if (currentTime < DecoyConstants.TOTAL_DURATION_TICKS) {
                timestamps.add(currentTime)
            }
        }
        return timestamps
    }

    /**
     * 生成fallback射击时间戳
     */
    private fun generateFallbackTimestamps(): List<Int> = generateSemiTimestamps()

    /**
     * 获取随机爆发延迟
     * 核心逻辑：第一次爆发使用较短延迟，增加真实性
     */
    private fun getRandomBurstDelay(isFirstBurst: Boolean): Int {
        // 第一次爆发使用较短延迟范围
        val minDelay = if (isFirstBurst) DecoyConstants.MIN_BURST_DELAY_TICKS / 2 else DecoyConstants.MIN_BURST_DELAY_TICKS
        val maxDelay = if (isFirstBurst) DecoyConstants.MAX_BURST_DELAY_TICKS / 2 else DecoyConstants.MAX_BURST_DELAY_TICKS
        // 随机生成延迟
        return Random.nextInt(minDelay, maxDelay + 1)
    }
}
