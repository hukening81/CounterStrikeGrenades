@file:Suppress("INFERRED_INVISIBLE_RETURN_TYPE_WARNING")

package club.pisquad.minecraft.csgrenades.network

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.SERVER_MESSAGE_RANGE
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.FireGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangPacketHandler
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.HEGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.SmokeGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.ClientGrenadeThrowMessage
import club.pisquad.minecraft.csgrenades.network.message.ServerGrenadeBlockBounceSoundMessage
import club.pisquad.minecraft.csgrenades.network.message.ServerGrenadeMovementSyncMessage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.serializer
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3
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
        val byteArray = ProtoBuf.encodeToByteArray(messageClass.serializer(), message)
        buffer.writeByteArray(byteArray)
    }

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    fun decoder(buffer: FriendlyByteBuf): Msg =
        ProtoBuf.decodeFromByteArray(messageClass.serializer(), buffer.readByteArray())

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

    fun register() {
        ModLogger.info("Registering network packets")
        registerMessage(
            ClientGrenadeThrowMessage::class.java,
            ClientGrenadeThrowMessage::encoder,
            ClientGrenadeThrowMessage::decoder,
            ClientGrenadeThrowMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_SERVER),
        )
        registerMessage(
            ServerGrenadeMovementSyncMessage::class.java,
            ServerGrenadeMovementSyncMessage::encoder,
            ServerGrenadeMovementSyncMessage::decoder,
            ServerGrenadeMovementSyncMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
        registerMessage(
            ServerGrenadeBlockBounceSoundMessage::class.java,
            ServerGrenadeBlockBounceSoundMessage::encoder,
            ServerGrenadeBlockBounceSoundMessage::decoder,
            ServerGrenadeBlockBounceSoundMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        )

        HEGrenadePacketHandler.registerMessages(this)
        FireGrenadePacketHandler.registerMessages(this)
        SmokeGrenadePacketHandler.registerMessages(this)
        FlashbangPacketHandler.registerMessages(this)
    }

    fun <M : Any> registerMessage(
        message: Class<M>,
        encoder: BiConsumer<M, FriendlyByteBuf>,
        decoder: Function1<FriendlyByteBuf, M>,
        consumer: BiConsumer<M, Supplier<NetworkEvent.Context>>,
        direction: Optional<NetworkDirection>
    ) {
        INSTANCE.registerMessage(messageTypeCount, message, encoder, decoder, consumer, direction)
    }

    fun sendMessageToPlayer(level: ServerLevel, position: Vec3, message: Any) {
        level.players().forEach {
            if (it.position().distanceTo(position) < SERVER_MESSAGE_RANGE) {
                INSTANCE.send(PacketDistributor.PLAYER.with { it }, message)
            }
        }
    }
}
