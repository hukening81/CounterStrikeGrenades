package club.pisquad.minecraft.csgrenades.entity.smokegrenade

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.cbor.Cbor
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.syncher.EntityDataSerializer

class SmokeDataPointSetEntityDataSerializer : EntityDataSerializer<Set<SmokeDataPoint>> {
    @OptIn(ExperimentalSerializationApi::class)
    override fun write(pBuffer: FriendlyByteBuf, pValue: Set<SmokeDataPoint>) {
        val byteArray = Cbor.Default.encodeToByteArray(SetSerializer(SmokeDataPoint.serializer()), pValue)
        pBuffer.writeByteArray(byteArray)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun read(pBuffer: FriendlyByteBuf): Set<SmokeDataPoint> {
        val byteArray = pBuffer.readByteArray()
        return Cbor.Default.decodeFromByteArray(SetSerializer(SmokeDataPoint.serializer()), byteArray)
    }

    override fun copy(pValue: Set<SmokeDataPoint>): Set<SmokeDataPoint> = pValue
}
