package club.pisquad.minecraft.csgrenades.serializer

import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.json.Json
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.syncher.EntityDataSerializer

class BlockPosEntityDataSerializer : EntityDataSerializer<BlockPos> {
    override fun write(pBuffer: FriendlyByteBuf, pValue: BlockPos) {
        pBuffer.writeUtf(Json.encodeToString(IntArraySerializer(), arrayOf(pValue.x, pValue.y, pValue.z).toIntArray()))
    }

    override fun read(pBuffer: FriendlyByteBuf): BlockPos {
        return Json.decodeFromString<BlockPos>(pBuffer.readUtf())
    }

    override fun copy(pValue: BlockPos): BlockPos {
        return pValue
    }
}