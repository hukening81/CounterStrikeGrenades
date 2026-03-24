package club.pisquad.minecraft.csgrenades.api

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.network.serializer.UUIDSerializer
import club.pisquad.minecraft.csgrenades.network.serializer.Vec3Serializer
import kotlinx.serialization.Serializable
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.Vec3
import java.util.*

/**
 * This API should only be called on server side for consistant behavior
 */
object CSGrenadesAPI {
    /**
     * Spawn a grenade with the provide context
     *
     * @param context
     * @return spanwed entity if success
     */
    fun spawnGrenade(
        owner: ServerPlayer,
        context: GrenadeSpawnContext,
        removeItem: Boolean = true
    ): CounterStrikeGrenadeEntity? {
        val level = owner.level() as ServerLevel
        val entityType = context.grenadeType.entity.get()
        val entity = entityType.create(level) ?: return null
        entity.initialize(context.ownerUuid, context.position, context.velocity)

        ModLogger.info("Spawning ${context.grenadeType} entity at ${context.position} with velocity ${context.velocity.length()} blocks per tick")
        level.addFreshEntity(entity)

        if (removeItem) {
            Player.removeGrenadeFromInventory(owner, context.grenadeType)
        }
        return entity
    }

    object Player {
        fun removeGrenadeFromInventory(player: ServerPlayer, grenadeType: GrenadeType): Boolean {
            val item = player.mainHandItem.item
            if (item is CounterStrikeGrenadeItem && item.grenadeType == grenadeType) {
                player.mainHandItem.count--
            } else {
                player.inventory.items.forEach {
                    val item = it.item
                    if (item is CounterStrikeGrenadeItem && item.grenadeType == grenadeType) {
                        it.count--
                        return true
                    }
                }
            }
            return false
        }
    }

    object Grenade {
        // Use this field direcly is on your own risk
        val grenades: MutableMap<UUID, CounterStrikeGrenadeEntity> = mutableMapOf()

        internal fun register(entity: CounterStrikeGrenadeEntity) {
            grenades[entity.uuid] = entity
        }

        internal fun unregister(uuid: UUID): CounterStrikeGrenadeEntity? {
            return grenades.remove(uuid)
        }

        fun get(grenadeType: GrenadeType): List<CounterStrikeGrenadeEntity> {
            return grenades.filter { it.value.grenadeType == grenadeType }.map { it.value }
        }
    }
}

@Serializable
data class GrenadeSpawnContext(
    val grenadeType: GrenadeType,
    @Serializable(with = UUIDSerializer::class) val ownerUuid: UUID,
    @Serializable(with = Vec3Serializer::class) val position: Vec3,
    @Serializable(with = Vec3Serializer::class) val velocity: Vec3,
)
///**
// * 提供了与CS手榴弹模组交互的公共API。
// */
//object CSGrenadesAPI {
//
//    // 存储被闪光弹致盲的玩家UUID及其剩余的致盲时间（tick）
//    private val flashedPlayers: ConcurrentHashMap<UUID, Int> = ConcurrentHashMap()
//
//    /**
//     * 检查给定的玩家是否当前被CS手榴弹模组的闪光弹致盲。
//     * @param player 要检查的玩家。
//     * @return 如果玩家被致盲，则返回 true；否则返回 false。
//     */
//    @JvmStatic
//    fun isPlayerFlashed(player: Player): Boolean =
//        flashedPlayers.containsKey(player.uuid) && (flashedPlayers[player.uuid] ?: 0) > 0
//
//    /**
//     * 内部方法：设置玩家的闪光弹致盲状态和持续时间。
//     * 仅供CS手榴弹模组内部使用。
//     * @param player 被致盲的玩家。
//     * @param durationTicks 致盲持续时间，单位是游戏刻 (tick)。
//     */
//    internal fun setPlayerFlashed(player: Player, durationTicks: Int) {
//        if (durationTicks > 0) {
//            flashedPlayers[player.uuid] = durationTicks
//        } else {
//            flashedPlayers.remove(player.uuid)
//        }
//    }
//
//    /**
//     * 内部方法：在每个服务器tick更新玩家的致盲状态。
//     * 仅供CS手榴弹模组内部使用。
//     */
//    internal fun onServerTick() {
//        flashedPlayers.forEach { (uuid, duration) ->
//            if (duration <= 1) { // 如果持续时间小于等于1，则移除
//                flashedPlayers.remove(uuid)
//            } else {
//                flashedPlayers[uuid] = duration - 1
//            }
//        }
//    }
//}
