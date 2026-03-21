package club.pisquad.minecraft.csgrenades.grenades.hegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem

class HEGrenadeItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    //    override val sounds: GrenadeItemSoundEvents = GrenadeItemSoundEvents(ModSoundEvents.hegrenade.draw)
    override val grenadeType: GrenadeType = GrenadeType.HE_GRENADE
}