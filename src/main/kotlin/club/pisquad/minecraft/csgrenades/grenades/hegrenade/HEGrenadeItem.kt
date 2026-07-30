package club.pisquad.minecraft.csgrenades.grenades.hegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem

class HEGrenadeItem() : CounterStrikeGrenadeItem(Properties().stacksTo(1)) {
    //    override val sounds: GrenadeItemSoundEvents = GrenadeItemSoundEvents(ModSoundEvents.hegrenade.draw)
    override val grenadeType: GrenadeType = GrenadeType.HE_GRENADE
}