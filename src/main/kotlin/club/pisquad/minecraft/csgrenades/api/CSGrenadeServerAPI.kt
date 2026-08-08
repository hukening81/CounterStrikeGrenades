package club.pisquad.minecraft.csgrenades.api

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.api.data.GrenadeSpawnContext
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.hurtCancelKnockback
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.network.message.ServerGrenadeHitBlockMessage
import club.pisquad.minecraft.csgrenades.network.message.ServerGrenadeHitEntityMessage
import club.pisquad.minecraft.csgrenades.physics.GrenadePosition
import club.pisquad.minecraft.csgrenades.physics.GrenadeVelocity
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import java.util.*

object CSGrenadeServerAPI {
    val player = CSGrenadeServerPlayerAPI
    val entity = CSGrenadeEntityAPI

    object CSGrenadeEntityAPI {
        /**
         * Spawn a grenade with the provide context
         *
         * @param context
         * @return spawned entity if success
         */
        fun spawnGrenade(
            owner: ServerPlayer, context: GrenadeSpawnContext, removeItem: Boolean = true
        ): CounterStrikeGrenadeEntity? {
            val level = owner.level() as ServerLevel
            val entityRegistryObject = context.grenadeType.properties.entity
            if (!entityRegistryObject.isPresent) {
                return null
            }
            val entityType = entityRegistryObject.get()
            val entity = entityType.create(level) ?: return null
            entity.ownerUuid = owner.uuid
            entity.setPos(GrenadePosition.fromCenter(context.position).worldPos)
            entity.deltaMovement = GrenadeVelocity.fromBlocksPerTick(context.velocity).blocksPerTick

            ModLogger.info("Spawning ${context.grenadeType} entity at ${context.position} with velocity ${context.velocity.length()} blocks per tick")
            level.addFreshEntity(entity)

            if (removeItem) {
                CSGrenadeServerPlayerAPI.removeGrenadeFromInventory(owner, context.grenadeType)
            }
            return entity
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

        @Suppress("unused")
        fun setInventoryCoolDown(player: ServerPlayer) {
            val amount = ModConfig.throwConfig.cooldown.get().toTick().toInt()
            player.inventory.items.forEach {
                if (it.item is CounterStrikeGrenadeItem) {
                    player.cooldowns.addCooldown(it.item, amount)
                }
            }
        }
    }


    fun dealHitDamage(grenade: CounterStrikeGrenadeEntity, target: LivingEntity, damageAmount: Double) {
        val damageTypeKey = grenade.grenadeType.properties.damageTypes.hit
        val registry = grenade.level().registryAccess().registry(Registries.DAMAGE_TYPE).get()
        val damageType = Holder.direct(registry.get(damageTypeKey)!!)

        target.hurtCancelKnockback(DamageSource(damageType, target, grenade, grenade.center), damageAmount)
    }

    fun sendGrenadeHitEntityMessage(level: ServerLevel, message: ServerGrenadeHitEntityMessage) {
        ModPacketHandler.sendMessageToPlayer(level, message.hitPoint, message)
    }

    fun sendGrenadeHitBlockMessage(level: ServerLevel, message: ServerGrenadeHitBlockMessage) {
        ModPacketHandler.sendMessageToPlayer(level, message.hitPoint, message)

    }
}