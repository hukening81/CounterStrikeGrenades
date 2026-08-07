package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.ModSettings
import club.pisquad.minecraft.csgrenades.command.ModCommands
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.INCENDIARY_RESOURCE_KEY
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryConfig
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryEntity
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryItem
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.messages.ServerFireGrenadeActivatedMessage
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MOLOTOV_RESOURCE_KEY
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovConfig
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovEntity
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovItem
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.renderer.FireRegionDebugRenderer
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.registry.ModEntities
import club.pisquad.minecraft.csgrenades.registry.ModItems
import club.pisquad.minecraft.csgrenades.registry.ModRenderers
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraftforge.network.NetworkDirection
import java.util.*

object FireGrenadeRegistryHelper {
    val incendiaryEntity = ModEntities.registerGrenadeEntity(INCENDIARY_RESOURCE_KEY, ::IncendiaryEntity)
    val incendiaryItem = ModItems.registerGrenadeItem(INCENDIARY_RESOURCE_KEY) { IncendiaryItem() }


    val molotovEntity = ModEntities.registerGrenadeEntity(MOLOTOV_RESOURCE_KEY, ::MolotovEntity)
    val molotovItem = ModItems.registerGrenadeItem(MOLOTOV_RESOURCE_KEY) { MolotovItem() }

    val fireRegionEntity = ModEntities.ENTITIES.register("fire_region") {
        EntityType.Builder.of(::FireRegionEntity, MobCategory.MISC)
            .sized(
                ModSettings.Entity.GRENADE_ENTITY_SIZE.toFloat(),
                ModSettings.Entity.GRENADE_ENTITY_SIZE.toFloat(),
            )
            .updateInterval(1)
            .build(ResourceLocation(CounterStrikeGrenades.ID, "fire_region").toString())
    }

    @Suppress("unused")
    private val _particle = FlameParticleRegistry


    init {
        ModConfig.addSection("molotov", MolotovConfig)
        ModConfig.addSection("incendiary", IncendiaryConfig)

        ModRenderers.addDefferedRegisterEntityRendererTask {
            EntityRenderers.register(this.fireRegionEntity.get(), ::FireRegionDebugRenderer)
        }

        ModCommands.addRegisterTask(FireGrenadeCommands::register)

        ModPacketHandler.registerMessage(
            ServerFireGrenadeActivatedMessage::class.java,
            ServerFireGrenadeActivatedMessage::encoder,
            ServerFireGrenadeActivatedMessage::decoder,
            ServerFireGrenadeActivatedMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        )
    }
}