package club.pisquad.minecraft.csgrenades.api.data

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.world.phys.Vec3
import java.util.*

@Serializable
data class GrenadeSpawnContext(
    val grenadeType: GrenadeType,
    @Serializable(with = UUIDSerializer::class) val ownerUuid: UUID,
    @Serializable(with = Vec3Serializer::class) val position: Vec3,
    @Serializable(with = Vec3Serializer::class) val velocity: Vec3,
)