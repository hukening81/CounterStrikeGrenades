@file:Suppress("INFERRED_INVISIBLE_RETURN_TYPE_WARNING")

package club.pisquad.minecraft.csgrenades.network

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.network.message.FlashBangExplodedMessage
import club.pisquad.minecraft.csgrenades.network.message.GrenadeThrownMessage
import club.pisquad.minecraft.csgrenades.network.message.firegrenade.FireGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.hegrenade.HEGrenadePacketHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Supplier

private const val PROTOCOL_VERSION = "1"

interface CsGrenadeMessageHandler<Msg> {
    fun encoder(message: Msg, buffer: FriendlyByteBuf)
    fun decoder(buffer: FriendlyByteBuf): Msg
    fun handler(msg: Msg, ctx: Supplier<NetworkEvent.Context>)
}

interface CsGrenadePacketHandler {
    fun registerMessages()
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

    fun registerMessage() {
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
        HEGrenadePacketHandler.registerMessages()
        FireGrenadePacketHandler.registerMessages()
    }

    fun <M : Any> registerSingleMessage(message: Class<M>, encoder: BiConsumer<M, FriendlyByteBuf>, decoder: Function1<FriendlyByteBuf, M>, consumer: BiConsumer<M, Supplier<NetworkEvent.Context>>, direction: Optional<NetworkDirection>) {
        INSTANCE.registerMessage(messageTypeCount, message, encoder, decoder, consumer, direction)
    }
}
