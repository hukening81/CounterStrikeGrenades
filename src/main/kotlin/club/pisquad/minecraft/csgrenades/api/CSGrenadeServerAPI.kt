package club.pisquad.minecraft.csgrenades.api

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.api.data.GrenadeSpawnContext
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.network.message.ServerGrenadeBlockBounceSoundMessage
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.Vec3
import java.util.*

object CSGrenadeServerAPI {
    val sound = CSGrenadeServerSoundAPI
    val player = CSGrenadeServerPlayerAPI
    val entity = CSGrenadeEntityAPI

    object CSGrenadeEntityAPI {
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
                CSGrenadeServerPlayerAPI.removeGrenadeFromInventory(owner, context.grenadeType)
            }
            return entity
        }
    }


    object CSGrenadeServerSoundAPI {
        fun playHitBlockSound(grenade: GrenadeType, uuid: UUID, level: ServerLevel, position: Vec3) {
            ModPacketHandler.sendMessageToPlayer(
                level,
                position,
                ServerGrenadeBlockBounceSoundMessage(grenade, uuid, position)
            )
        }
    }

    object CSGrenadeServerPlayerAPI {
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

        fun setInventoryCoolDown(player: ServerPlayer) {
            val amount = ModConfig.throwConfig.cooldown.get().toTick().toInt()
            player.inventory.items.forEach {
                if (it.item is CounterStrikeGrenadeItem) {
                    player.cooldowns.addCooldown(it.item, amount)
                }
            }
        }
    }
}