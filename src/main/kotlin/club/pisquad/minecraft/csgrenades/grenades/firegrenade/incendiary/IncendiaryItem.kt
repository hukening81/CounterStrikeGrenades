package club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem

class IncendiaryItem() : CounterStrikeGrenadeItem(Properties().stacksTo(1)) {
    override val grenadeType: GrenadeType = GrenadeType.INCENDIARY
}
