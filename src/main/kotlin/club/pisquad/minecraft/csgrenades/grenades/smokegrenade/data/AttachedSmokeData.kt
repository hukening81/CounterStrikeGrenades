package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.data

import club.pisquad.minecraft.csgrenades.network.serializer.BlockPosSerializer
import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos

@Serializable
sealed interface AttachedSmokeData {
    @Serializable
    class EmptySmokeData : AttachedSmokeData

    @Serializable
    data class SmokeData(
        val activationTime: Long,
        val voxels: Map<@Serializable(
            with = BlockPosSerializer::class
        ) BlockPos, Int>
    ) : AttachedSmokeData {
    }
}