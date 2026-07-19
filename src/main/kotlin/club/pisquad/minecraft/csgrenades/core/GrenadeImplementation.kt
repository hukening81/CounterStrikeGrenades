package club.pisquad.minecraft.csgrenades.core

import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.registry.ModEntities
import club.pisquad.minecraft.csgrenades.registry.ModItems
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.EntityType
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.registries.RegistryObject

interface GrenadeCommonSounds {
    val draw: GrenadeSoundData
    val hitBlock: GrenadeSoundData
    val hitEntity: GrenadeSoundData
    val `throw`: GrenadeSoundData
    val pinPullStart: GrenadeSoundData
    val pinPull: GrenadeSoundData
}

interface GrenadeCommonDamageTypes {
    val hit: ResourceKey<DamageType>
}

interface GrenadeImplementation {

    val resourceKey: String

    fun getCommonSounds(): GrenadeCommonSounds

    fun getCommonDamageTypes(): GrenadeCommonDamageTypes

    fun getEntity(): RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>>

    fun getItem(): RegistryObject<out CounterStrikeGrenadeItem>

    fun buildConfig(builder: ForgeConfigSpec.Builder)

    fun registerItems(modItems: ModItems)

    fun registerEntities(modEntities: ModEntities)

    fun registerNetworkMessages(modPacketHandler: ModPacketHandler)

    fun registerEntityDataSerializers()

}