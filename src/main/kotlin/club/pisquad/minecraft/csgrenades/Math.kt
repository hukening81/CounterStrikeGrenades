package club.pisquad.minecraft.csgrenades

import net.minecraft.world.phys.Vec3

fun Vec3.minus(other: Vec3): Vec3 = this.add(other.reverse())
fun Vec3.div(factor: Double): Vec3 = this.scale(1.div(factor))
