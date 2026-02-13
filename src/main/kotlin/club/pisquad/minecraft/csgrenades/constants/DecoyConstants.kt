package club.pisquad.minecraft.csgrenades.constants

/**
 * 诱饵弹相关常量
 */
object DecoyConstants {
    // 诱饵弹总持续时间（15秒）
    const val TOTAL_DURATION_TICKS = 15 * 20

    // 最小爆发延迟（25 ticks ≈ 1.25秒）
    const val MIN_BURST_DELAY_TICKS = 25

    // 最大爆发延迟（60 ticks ≈ 3秒）
    const val MAX_BURST_DELAY_TICKS = 60

    // 爆发中最小射击次数
    const val MIN_SHOTS_IN_BURST = 2

    // 爆发中最大射击次数
    const val MAX_SHOTS_IN_BURST = 5
}
