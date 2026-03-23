package club.pisquad.minecraft.csgrenades.grenades.smokegrenade.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.protobuf.ProtoBuf
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.syncher.EntityDataSerializer

class SmokeDataPointSetEntityDataSerializer : EntityDataSerializer<Set<SmokeDataPoint>> {
    @OptIn(ExperimentalSerializationApi::class)
    override fun write(pBuffer: FriendlyByteBuf, pValue: Set<SmokeDataPoint>) {
        val byteArray = ProtoBuf.Default.encodeToByteArray(SetSerializer(SmokeDataPoint.serializer()), pValue)
        pBuffer.writeByteArray(byteArray)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun read(pBuffer: FriendlyByteBuf): Set<SmokeDataPoint> {
        val byteArray = pBuffer.readByteArray()
        return ProtoBuf.Default.decodeFromByteArray(SetSerializer(SmokeDataPoint.serializer()), byteArray)
    }

    override fun copy(pValue: Set<SmokeDataPoint>): Set<SmokeDataPoint> = pValue
}
