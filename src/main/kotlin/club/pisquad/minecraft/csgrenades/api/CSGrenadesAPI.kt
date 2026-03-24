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


    // With this convention, I hope to minimize namespace contamination
    val player = CSGrenadePlayerAPI
    val sound = CSGrenadeSoundAPI

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
            player.removeGrenadeFromInventory(owner, context.grenadeType)
        }
        return entity
    }

    object CSGrenadePlayerAPI {
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