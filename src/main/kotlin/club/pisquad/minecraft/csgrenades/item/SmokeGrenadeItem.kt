package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.enums.*
import club.pisquad.minecraft.csgrenades.registry.*

class SmokeGrenadeItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    init {
        drawSound = ModSoundEvents.SMOKE_GRENADE_DRAW.get()
        grenadeType = GrenadeType.SMOKE_GRENADE
    }
}
