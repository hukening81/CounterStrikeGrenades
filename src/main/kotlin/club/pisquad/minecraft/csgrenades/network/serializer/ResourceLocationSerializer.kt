package club.pisquad.minecraft.csgrenades.network.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.resources.ResourceLocation

class ResourceLocationSerializer : KSerializer<ResourceLocation> {
    private val delegateSerializer = String.serializer()

    override val descriptor: SerialDescriptor = SerialDescriptor("ResourceLocation", delegateSerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: ResourceLocation
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ResourceLocation {
        return ResourceLocation.parse(decoder.decodeString())
    }
}