package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GRENADE_ENTITY_SIZE
import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModEntities {
    const val ENTITY_SIZE = GRENADE_ENTITY_SIZE.toFloat()
    val ENTITIES: DeferredRegister<EntityType<*>> =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CounterStrikeGrenades.ID)

//    val FLASH_BANG_ENTITY = registerSingle("flashbang", ::FlashBangEntity)
//
//    val SMOKE_GRENADE_ENTITY = registerSingle("smokegrenade", ::SmokeGrenadeEntity)
//
//    val HEGRENADE_ENTITY = registerSingle("hegrenade", ::HEGrenadeEntity)
//
//    val INCENDIARY_ENTITY = registerSingle("incendiary", ::IncendiaryEntity)
//
//    val MOLOTOV_ENTITY = registerSingle("molotov", ::MolotovEntity)
//
//    val DECOY_ENTITY = registerSingle("decoy", ::DecoyGrenadeEntity)

    fun register(bus: IEventBus) {
        ModLogger.info("Registering entities")
        ENTITIES.register(bus)
    }

    fun <T : CounterStrikeGrenadeEntity> registerSingle(
        name: String,
        factory: EntityType.EntityFactory<T>
    ): RegistryObject<EntityType<T>> {
        return ENTITIES.register(name) {
            EntityType.Builder.of(
                factory,
                MobCategory.MISC,
            ).sized(ENTITY_SIZE, ENTITY_SIZE).updateInterval(1).setShouldReceiveVelocityUpdates(false)
                .build(ResourceLocation(CounterStrikeGrenades.ID, name).toString())
        }
    }
}

