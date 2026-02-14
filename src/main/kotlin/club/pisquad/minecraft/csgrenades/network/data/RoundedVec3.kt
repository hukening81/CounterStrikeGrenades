package club.pisquad.minecraft.csgrenades.network.data

import kotlinx.serialization.Serializable
import net.minecraft.world.phys.Vec3
import kotlin.math.pow
import kotlin.math.roundToInt

@Serializable
class RoundedDouble(val value: Long) {
    companion object {
        const val DECIMAL: Int = 2
        fun fromDouble(value: Double): RoundedDouble = RoundedDouble(value.times(10.0.pow(DECIMAL)).roundToInt().toLong())
    }

    fun toDouble(): Double = value.div(2.0)
}

fun Double.round(): RoundedDouble = RoundedDouble.fromDouble(this)
fun List<Double>.round(): List<RoundedDouble> = this.map { it.round() }

@Serializable
class RoundedVec3(
    val x: RoundedDouble,
    val y: RoundedDouble,
    val z: RoundedDouble,
) {
    companion object {
        fun fromVec3(value: Vec3): RoundedVec3 = RoundedVec3(value.x.round(), value.y.round(), value.z.round())
    }

    fun toVec3(): Vec3 = Vec3(x.toDouble(), y.toDouble(), z.toDouble())
}

// fun List<Vec3>.round() = this.map { RoundedVec3.fromVec3(it) }
// fun List<RoundedVec3>.toVec3List() = this.map { it.toVec3() }
