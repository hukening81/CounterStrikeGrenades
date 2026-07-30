package club.pisquad.minecraft.csgrenades.grenades.firegrenade

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.INCENDIARY_RESOURCE_KEY
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryConfig
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryEntity
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary.IncendiaryItem
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MOLOTOV_RESOURCE_KEY
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovConfig
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovEntity
import club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov.MolotovItem
import club.pisquad.minecraft.csgrenades.registry.ModEntities
import club.pisquad.minecraft.csgrenades.registry.ModItems

object FireGrenadeRegistryHelper {
    val incendiaryEntity = ModEntities.registerGrenadeEntity(INCENDIARY_RESOURCE_KEY, ::IncendiaryEntity)
    val incendiaryItem = ModItems.registerGrenadeItem(INCENDIARY_RESOURCE_KEY) { IncendiaryItem() }

    val molotovEntity = ModEntities.registerGrenadeEntity(MOLOTOV_RESOURCE_KEY, ::MolotovEntity)
    val molotovItem = ModItems.registerGrenadeItem(MOLOTOV_RESOURCE_KEY) { MolotovItem() }

    init {
        ModConfig.addSection("molotov", MolotovConfig)
        ModConfig.addSection("incendiary", IncendiaryConfig)
    }
}