package club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem

class MolotovItem() : CounterStrikeGrenadeItem(Properties().stacksTo(1)) {
    override val grenadeType: GrenadeType = GrenadeType.MOLOTOV
}
