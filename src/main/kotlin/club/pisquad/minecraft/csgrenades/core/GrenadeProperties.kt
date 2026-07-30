package club.pisquad.minecraft.csgrenades.core

import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import net.minecraft.world.entity.EntityType
import net.minecraftforge.registries.RegistryObject

// Type erased grenade information that can be used in common functions
interface GrenadeProperties {
    val entity: RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>>
    val item: RegistryObject<out CounterStrikeGrenadeItem>
    val resourceKey: String
    val sounds: GrenadeCommonSounds
    val damageTypes: GrenadeCommonDamageTypes
}