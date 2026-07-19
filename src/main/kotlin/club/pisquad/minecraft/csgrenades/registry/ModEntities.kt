package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.ModSettings.Entity.GRENADE_ENTITY_SIZE
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import kotlin.reflect.KClass

object ModEntities {
    val ENTITIES: DeferredRegister<EntityType<*>> =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CounterStrikeGrenades.ID)

    fun register(bus: IEventBus) {
        ModLogger.info("Registering entities")
        ENTITIES.register(bus)
    }

    fun <T : CounterStrikeGrenadeEntity> registerGrenadeEntity(
        resourceKey: String,
        factory: EntityType.EntityFactory<T>
    ): RegistryObject<EntityType<T>> {
        return ENTITIES.register(resourceKey) {
            EntityType.Builder.of(
                factory,
                MobCategory.MISC,
            ).sized(GRENADE_ENTITY_SIZE.toFloat(), GRENADE_ENTITY_SIZE.toFloat())
                .updateInterval(1)
                .build(ResourceLocation(CounterStrikeGrenades.ID, resourceKey).toString())
        }
    }
}

