package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem

class SmokeGrenadeItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    //    override val sounds: GrenadeItemSoundEvents = GrenadeItemSoundEvents(ModSoundEvents.smokegrenade.draw)
    override val grenadeType: GrenadeType = GrenadeType.SMOKE_GRENADE
}