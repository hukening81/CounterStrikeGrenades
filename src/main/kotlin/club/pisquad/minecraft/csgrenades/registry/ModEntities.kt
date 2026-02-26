package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GRENADE_ENTITY_SIZE
import club.pisquad.minecraft.csgrenades.entity.core.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.entity.decoy.DecoyGrenadeEntity
import club.pisquad.minecraft.csgrenades.entity.firegrenade.IncendiaryEntity
import club.pisquad.minecraft.csgrenades.entity.firegrenade.MolotovEntity
import club.pisquad.minecraft.csgrenades.entity.hegrenade.FlashBangEntity
import club.pisquad.minecraft.csgrenades.entity.hegrenade.HEGrenadeEntity
import club.pisquad.minecraft.csgrenades.entity.smokegrenade.SmokeGrenadeEntity
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

    //    val FLASH_BANG_ENTITY: RegistryObject<EntityType<FlashBangEntity>> = ENTITIES.register("flashbang") {
//        EntityType.Builder.of(
//            { pEntityType: EntityType<FlashBangEntity>, pLevel: Level -> FlashBangEntity(pEntityType, pLevel) },
//            MobCategory.MISC,
//        ).sized(ENTITY_SIZE, ENTITY_SIZE).updateInterval(Int.MAX_VALUE)
//            .build(ResourceLocation(CounterStrikeGrenades.ID, "flashbang").toString())
//    }
    val FLASH_BANG_ENTITY = registerGrenade("flashbang", ::FlashBangEntity)

    //    val SMOKE_GRENADE_ENTITY: RegistryObject<EntityType<SmokeGrenadeEntity>> = ENTITIES.register("smokegrenade") {
//        EntityType.Builder.of(
//            { pEntityType: EntityType<SmokeGrenadeEntity>, pLevel: Level -> SmokeGrenadeEntity(pEntityType, pLevel) },
//            MobCategory.MISC,
//        ).sized(ENTITY_SIZE, ENTITY_SIZE).updateInterval( Int.MAX_VALUE)
//            .build(ResourceLocation(CounterStrikeGrenades.ID, "smokegrenade").toString())
//    }
    val SMOKE_GRENADE_ENTITY = registerGrenade("smokegrenade", ::SmokeGrenadeEntity)

    //    val HEGRENADE_ENTITY: RegistryObject<EntityType<HEGrenadeEntity>> = ENTITIES.register("hegrenade") {
//        EntityType.Builder.of(
//            { pEntityType: EntityType<HEGrenadeEntity>, pLevel: Level -> HEGrenadeEntity(pEntityType, pLevel) },
//            MobCategory.MISC,
//        ).sized(ENTITY_SIZE, ENTITY_SIZE).updateInterval( Int.MAX_VALUE)
//            .build(ResourceLocation(CounterStrikeGrenades.ID, "hegrenade").toString())
//    }
    val HEGRENADE_ENTITY = registerGrenade("hegrenade", ::HEGrenadeEntity)
    val INCENDIARY_ENTITY = registerGrenade("incendiary", ::IncendiaryEntity)

//    val INCENDIARY_ENTITY: RegistryObject<EntityType<IncendiaryEntity>> = ENTITIES.register("incendiary") {
//        EntityType.Builder.of(
//            { pEntityType: EntityType<IncendiaryEntity>, pLevel: Level -> IncendiaryEntity(pEntityType, pLevel) },
//            MobCategory.MISC,
//        ).sized(ENTITY_SIZE, ENTITY_SIZE).updateInterval( Int.MAX_VALUE)
//            .build(ResourceLocation(CounterStrikeGrenades.ID, "incendiary").toString())
//    }

    val MOLOTOV_ENTITY = registerGrenade("molotov", ::MolotovEntity)

    val DECOY_ENTITY = registerGrenade("decoy", ::DecoyGrenadeEntity)

    fun register(bus: IEventBus) {
        ENTITIES.register(bus)
    }

    private fun <T : CounterStrikeGrenadeEntity> registerGrenade(name: String, factory: EntityType.EntityFactory<T>): RegistryObject<EntityType<T>> {
        return ENTITIES.register(name) {
            EntityType.Builder.of(
                factory,
                MobCategory.MISC,
            ).sized(ENTITY_SIZE, ENTITY_SIZE).updateInterval(Int.MAX_VALUE).setShouldReceiveVelocityUpdates(false)
                .build(ResourceLocation(CounterStrikeGrenades.ID, name).toString())
        }
    }
}

