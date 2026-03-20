package club.pisquad.minecraft.csgrenades.network.message

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.client.input.InputState.calculateGrenadeSpeed
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.enums.toModEntity
import club.pisquad.minecraft.csgrenades.getShootOrigin
import club.pisquad.minecraft.csgrenades.item.core.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.network.CsGrenadeMessageHandler
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

@Serializable
data class ClientGrenadeThrowMessage(
    @Serializable(with = UUIDSerializer::class) val ownerUuid: UUID,
    @Serializable(with = Vec3Serializer::class) val position: Vec3,
    @Serializable(with = Vec3Serializer::class) val velocity: Vec3,
    val grenadeType: GrenadeType,
) {
    companion object : CsGrenadeMessageHandler<ClientGrenadeThrowMessage>(ClientGrenadeThrowMessage::class) {
        override fun handler(msg: ClientGrenadeThrowMessage, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            val player = context.sender ?: return

            if (!player.isCreative) {
                removeGrenadeFromInventory(player, msg.grenadeType)
            }
            spawnGrenadeEntity(player.level(), msg)
        }

        private fun removeGrenadeFromInventory(player: ServerPlayer, grenadeType: GrenadeType) {
            val item = player.mainHandItem.item
            if (item is CounterStrikeGrenadeItem && item.grenadeType == grenadeType) {
                player.mainHandItem.count--
            } else {
                player.inventory.items.forEach {
                    val item = it.item
                    if (item is CounterStrikeGrenadeItem && item.grenadeType == grenadeType) {
                        it.count--
                        return
                    }
                }
            }
        }

        private fun spawnGrenadeEntity(level: Level, msg: ClientGrenadeThrowMessage) {
            val entityType = msg.grenadeType.toModEntity()
            val entity = entityType.create(level) ?: return
            entity.initialize(msg.ownerUuid, msg.position, msg.velocity)
            ModLogger.info("Spawning ${msg.grenadeType} entity at ${msg.position} with velocity ${msg.velocity.length()} blocks per tick")
            level.addFreshEntity(entity)
        }

        fun fromInputState(): ClientGrenadeThrowMessage? {
            val player = Minecraft.getInstance().player ?: return null
            val velocity = player.deltaMovement.add(calculateGrenadeSpeed() ?: return null)
            val position = player.getShootOrigin()
            val item = player.mainHandItem.item
            if (item !is CounterStrikeGrenadeItem) return null
            val grenadeType = item.grenadeType

            return ClientGrenadeThrowMessage(player.uuid, position, velocity, grenadeType)

        }
    }
}
