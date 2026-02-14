package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.enums.*
import club.pisquad.minecraft.csgrenades.registry.*

class HEGrenadeItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {

    init {
        drawSound = ModSoundEvents.HEGRENADE_DRAW.get()
        grenadeType = GrenadeType.HE_GRENADE
    }
}
