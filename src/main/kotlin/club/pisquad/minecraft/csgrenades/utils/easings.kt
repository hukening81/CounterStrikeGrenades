package club.pisquad.minecraft.csgrenades.utils

fun easeOutQuart(x: Double): Double {
    return 1 - Math.pow(1 - x, 4.0);
}