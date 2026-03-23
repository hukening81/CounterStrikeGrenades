package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.network.serializer.BlockPosSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.protobuf.ProtoBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.syncher.EntityDataSerializer

class BlockPosListEntityDataSerializer : EntityDataSerializer<List<@Serializable BlockPos>> {
    @OptIn(ExperimentalSerializationApi::class)
    override fun write(pBuffer: FriendlyByteBuf, pValue: List<@Serializable BlockPos>) {
        val byteArray = ProtoBuf.encodeToByteArray(ListSerializer(BlockPosSerializer()), pValue)
        pBuffer.writeByteArray(byteArray)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun read(pBuffer: FriendlyByteBuf): List<BlockPos> {
        val byteArray = pBuffer.readByteArray()
        return ProtoBuf.decodeFromByteArray(ListSerializer(BlockPosSerializer()), byteArray)
    }

    override fun copy(pValue: List<BlockPos>): List<BlockPos> = pValue
}
