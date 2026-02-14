@file:Suppress("INFERRED_INVISIBLE_RETURN_TYPE_WARNING")

package club.pisquad.minecraft.csgrenades.network

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.network.message.FlashBangExplodedMessage
import club.pisquad.minecraft.csgrenades.network.message.GrenadeThrownMessage
import club.pisquad.minecraft.csgrenades.network.message.firegrenade.FireGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.hegrenade.HEGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.smokegrenade.SmokeGrenadePacketHandler
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.serializer
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Supplier
import kotlin.reflect.KClass

private const val PROTOCOL_VERSION = "1"

abstract class CsGrenadeMessageHandler<Msg : Any>(
    private val messageClass: KClass<Msg>,
) {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    fun encoder(message: Msg, buffer: FriendlyByteBuf) {
        val byteArray = Cbor.encodeToByteArray(messageClass.serializer(), message)
        buffer.writeByteArray(byteArray)
    }

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    fun decoder(buffer: FriendlyByteBuf): Msg = Cbor.decodeFromByteArray(messageClass.serializer(), buffer.readByteArray())

    abstract fun handler(msg: Msg, ctx: Supplier<NetworkEvent.Context>)
}

interface CsGrenadePacketHandler {
    fun registerMessages(handler: ModPacketHandler)
}

object ModPacketHandler {
    var messageTypeCount: Int = 1
        get() {
            field += 1
            return field
        }

    val INSTANCE: SimpleChannel = NetworkRegistry.newSimpleChannel(
        ResourceLocation(CounterStrikeGrenades.ID, "event"),
        { PROTOCOL_VERSION },
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals,
    )

    fun forgeRegisterMessages() {
        INSTANCE.registerMessage(
            messageTypeCount,
            GrenadeThrownMessage::class.java,
            GrenadeThrownMessage::encoder,
            GrenadeThrownMessage::decoder,
            GrenadeThrownMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_SERVER),
        )
        INSTANCE.registerMessage(
            messageTypeCount,
            FlashBangExplodedMessage::class.java,
            FlashBangExplodedMessage::encoder,
            FlashBangExplodedMessage::decoder,
            FlashBangExplodedMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
        HEGrenadePacketHandler.registerMessages(this)
        FireGrenadePacketHandler.registerMessages(this)
        SmokeGrenadePacketHandler.registerMessages(this)
    }

    fun <M : Any> registerMessage(message: Class<M>, encoder: BiConsumer<M, FriendlyByteBuf>, decoder: Function1<FriendlyByteBuf, M>, consumer: BiConsumer<M, Supplier<NetworkEvent.Context>>, direction: Optional<NetworkDirection>) {
        INSTANCE.registerMessage(messageTypeCount, message, encoder, decoder, consumer, direction)
    }

    fun sendMessageToPlayer(dimension: ResourceKey<Level>, message: Any) {
        INSTANCE.send(PacketDistributor.DIMENSION.with { dimension }, message)
    }
}
