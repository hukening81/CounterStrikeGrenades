package club.pisquad.minecraft.csgrenades.registry.damage

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.DamageType

object ModDamageTypes {
    val hegrenade = HEGrenadeDamageTypes
    val flashbang = FlashBangDamageTypes
    val molotov = MolotovDamageTypes
    val incendiary = IncendiaryDamageTypes
    val decoy = DecoyDamageTypes
    val smokegrenade = SmokeGrenadeDamageTypes

    fun registerSingle(path: String): ResourceKey<DamageType> {
        return ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation(
                CounterStrikeGrenades.ID, path,
            ),
        )
    }
}
