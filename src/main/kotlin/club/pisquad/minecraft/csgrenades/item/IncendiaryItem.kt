package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.enums.*
import club.pisquad.minecraft.csgrenades.registry.*

class IncendiaryItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    init {
        drawSound = ModSoundEvents.INCENDIARY_DRAW.get()
        grenadeType = GrenadeType.INCENDIARY
    }
}
