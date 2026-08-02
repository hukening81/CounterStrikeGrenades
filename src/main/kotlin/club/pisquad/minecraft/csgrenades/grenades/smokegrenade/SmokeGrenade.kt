package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.ModSettings
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.CSGrenadeEntityDataSerializer
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonSounds
import club.pisquad.minecraft.csgrenades.core.GrenadeProperties
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.renderer.SmokeRegionRenderer
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelMap
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import club.pisquad.minecraft.csgrenades.registry.ModEntities
import club.pisquad.minecraft.csgrenades.registry.ModItems
import club.pisquad.minecraft.csgrenades.registry.ModRenderers
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraftforge.registries.RegistryObject

public const val T_SMOKE_RESOURCE_KEY = "smoke_t"
public const val CT_SMOKE_RESOURCE_KEY = "smoke_ct"

enum class SmokeGrenadeVariant {
    T, CT
}

object SmokeRegistryHelper {
    val tSmokeEntity = ModEntities.registerGrenadeEntity(T_SMOKE_RESOURCE_KEY, ::TSmokeGrenadeEntity)
    val tSmokeItem = ModItems.registerGrenadeItem(T_SMOKE_RESOURCE_KEY) { TSmokeGrenadeItem() }
    val ctSmokeEntity = ModEntities.registerGrenadeEntity(CT_SMOKE_RESOURCE_KEY, ::CTSmokeGrenadeEntity)
    val ctSmokeItem = ModItems.registerGrenadeItem(CT_SMOKE_RESOURCE_KEY) { CTSmokeGrenadeItem() }

    val smokeRegionEntity = ModEntities.ENTITIES.register("smoke_region") {
        EntityType.Builder.of(::SmokeRegionEntity, MobCategory.MISC)
            .sized(
                ModSettings.Entity.GRENADE_ENTITY_SIZE.toFloat(),
                ModSettings.Entity.GRENADE_ENTITY_SIZE.toFloat(),
            )
            .updateInterval(1)
            .build(ResourceLocation(CounterStrikeGrenades.ID, "smoke_region").toString())
    }

    @Suppress("unused")
    private val _particle = SmokeParticleRegistry

    init {
        ModConfig.addSection("smokegrenade", SmokeGrenadeConfig)
        ModRenderers.addDefferedRegisterEntityRendererTask {
            EntityRenderers.register(this.smokeRegionEntity.get(), ::SmokeRegionRenderer)
        }
    }
}

object TSmokeGrenadeProperties : GrenadeProperties {
    override val entity: RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> =
        SmokeRegistryHelper.tSmokeEntity
    override val item: RegistryObject<out CounterStrikeGrenadeItem> = SmokeRegistryHelper.tSmokeItem
    override val resourceKey: String = T_SMOKE_RESOURCE_KEY
    override val sounds: GrenadeCommonSounds = SmokeGrenadeSounds
    override val damageTypes: GrenadeCommonDamageTypes = SmokeGrenadeDamageTypes
}

object CTSmokeGrenadeProperties : GrenadeProperties {
    override val entity: RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> =
        SmokeRegistryHelper.ctSmokeEntity
    override val item: RegistryObject<out CounterStrikeGrenadeItem> = SmokeRegistryHelper.ctSmokeItem
    override val resourceKey: String = CT_SMOKE_RESOURCE_KEY
    override val sounds: GrenadeCommonSounds = SmokeGrenadeSounds
    override val damageTypes: GrenadeCommonDamageTypes = SmokeGrenadeDamageTypes
}

object SmokeGrenadeDamageTypes : GrenadeCommonDamageTypes {
    override val hit = ModDamageTypes.registerSingle("smokegrenade/hit")
}

object SmokeGrenadeSounds : GrenadeCommonSounds {
    override val draw = GrenadeSoundData.create("smokegrenade.draw")
    override val hitBlock = GrenadeSoundData.create("smokegrenade.bounce")
    override val hitEntity: GrenadeSoundData = ModSoundEvents.HIT_ENTITY
    override val `throw` = GrenadeSoundData.create("smokegrenade.throw")
    override val pinPull = GrenadeSoundData.create("smokegrenade.pinpull")
    override val pinPullStart = GrenadeSoundData.create("smokegrenade.pinpull_start")

    val can = GrenadeSoundData.create("smokegrenade.can")
    val explodeDistant = GrenadeSoundData.create("smokegrenade.explode_distant")
    val clear = GrenadeSoundData.create("smokegrenade.clear")
    val emit = GrenadeSoundData.create("smokegrenade.emit")
}

object SmokeGrenadeSerializers {
    val voxelMapSerializer = CSGrenadeEntityDataSerializer(VoxelMap.serializer())
}
