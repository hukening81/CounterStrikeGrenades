package club.pisquad.minecraft.csgrenades.core

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.protobuf.ProtoBuf
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.syncher.EntityDataSerializer

class CSGrenadeEntityDataSerializer<D : Any>(
    val serializer: KSerializer<D>
) : EntityDataSerializer<D> {

    @OptIn(ExperimentalSerializationApi::class)
    override fun write(pBuffer: FriendlyByteBuf, pValue: D) {
        pBuffer.writeByteArray(ProtoBuf.encodeToByteArray(serializer, pValue))
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun read(pBuffer: FriendlyByteBuf): D {
        return ProtoBuf.decodeFromByteArray(serializer, pBuffer.readByteArray())
    }

    override fun copy(pValue: D): D {
        return pValue
    }
}