package club.pisquad.minecraft.csgrenades.serializer

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.syncher.EntityDataSerializer

class BlockPosListEntityDataSerializer : EntityDataSerializer<List<@Serializable BlockPos>> {
    override fun write(pBuffer: FriendlyByteBuf, pValue: List<@Serializable BlockPos>) {
        val result = mutableListOf<IntArray>()
        pValue.forEach { result.add(arrayOf(it.x, it.y, it.z).toIntArray()) }
        pBuffer.writeUtf(Json.encodeToString(ListSerializer(IntArraySerializer()), result))
    }

    override fun read(pBuffer: FriendlyByteBuf): List<BlockPos> {

        val result = Json.decodeFromString<List<IntArray>>(pBuffer.readUtf())
        return result.map { BlockPos(it[0], it[1], it[2]) }
    }

    override fun copy(pValue: List<BlockPos>): List<BlockPos> {
        return pValue
    }
}