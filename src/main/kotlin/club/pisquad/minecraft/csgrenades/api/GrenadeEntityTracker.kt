package club.pisquad.minecraft.csgrenades.api

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.EntityLeaveLevelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

object ClientGrenadeEntityTracker {
    val trackedEntities: MutableSet<CounterStrikeGrenadeEntity> = mutableSetOf()

    fun getByType(vararg validTypes: GrenadeType): Iterable<CounterStrikeGrenadeEntity> {
        return this.trackedEntities.filter { it.grenadeType in validTypes }
    }

    fun getByID(id: Int): CounterStrikeGrenadeEntity? {
        return this.trackedEntities.find { it.id == id }
    }
}

object ServerGrenadeEntityTracker {
    val trackedEntities: MutableMap<ResourceKey<Level>, MutableSet<CounterStrikeGrenadeEntity>> = mutableMapOf()

    fun getByType(dimension: ResourceKey<Level>, vararg validTypes: GrenadeType): Iterable<CounterStrikeGrenadeEntity> {
        return this.trackedEntities.get(dimension)?.filter { it.grenadeType in validTypes }?:return emptyList()
    }

    fun getByID(dimension: ResourceKey<Level>, id: Int): CounterStrikeGrenadeEntity? {
        return this.trackedEntities.get(dimension)?.find { it.id == id }?:return null
    }
}


@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID)
private object EventHandler {
    @JvmStatic
    @SubscribeEvent
    fun onEntityJoin(event: EntityJoinLevelEvent) {
        if (event.entity !is CounterStrikeGrenadeEntity) {
            return
        }

        if (event.level.isClientSide) {
            ClientGrenadeEntityTracker.trackedEntities.add(event.entity as CounterStrikeGrenadeEntity)
        } else {
            ServerGrenadeEntityTracker.trackedEntities.getOrPut(
                event.level.dimension()
            ) { mutableSetOf() }.add(event.entity as CounterStrikeGrenadeEntity)
        }

    }

    @JvmStatic
    @SubscribeEvent
    fun onEntityLeave(event: EntityLeaveLevelEvent) {
        if (event.entity !is CounterStrikeGrenadeEntity) {
            return
        }
        if (event.level.isClientSide) {
            ClientGrenadeEntityTracker.trackedEntities.remove(event.entity)
        } else {
            ServerGrenadeEntityTracker.trackedEntities.get(event.level.dimension())?.run {
                this.remove(event.entity)
            }
        }

    }
}