package club.pisquad.minecraft.csgrenades.serializer

import club.pisquad.minecraft.csgrenades.network.serializer.BlockPosSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.syncher.EntityDataSerializer

class BlockPosListEntityDataSerializer : EntityDataSerializer<List<BlockPos>> {
    override fun write(pBuffer: FriendlyByteBuf, pValue: List<BlockPos>) {
        pBuffer.writeUtf(Json.encodeToString(ListSerializer(BlockPosSerializer()), pValue))
    }

    override fun read(pBuffer: FriendlyByteBuf): List<BlockPos> {
        return Json.decodeFromString<List<BlockPos>>(pBuffer.readUtf())
    }

    override fun copy(pValue: List<BlockPos>): List<BlockPos> {
        return pValue
    }
}