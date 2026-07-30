package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem

abstract class SmokeGrenadeItem : CounterStrikeGrenadeItem(Properties().stacksTo(1)) {
    abstract val variant: SmokeGrenadeVariant
}

class TSmokeGrenadeItem : SmokeGrenadeItem() {
    override val grenadeType: GrenadeType = GrenadeType.T_SMOKE
    override val variant: SmokeGrenadeVariant = SmokeGrenadeVariant.T
}

class CTSmokeGrenadeItem : SmokeGrenadeItem() {
    override val grenadeType: GrenadeType = GrenadeType.CT_SMOKE
    override val variant: SmokeGrenadeVariant = SmokeGrenadeVariant.CT
}