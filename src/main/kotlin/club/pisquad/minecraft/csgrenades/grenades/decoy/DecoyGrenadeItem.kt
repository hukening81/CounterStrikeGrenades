package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem

class DecoyGrenadeItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    //    override val sounds: GrenadeItemSoundEvents = GrenadeItemSoundEvents(ModSoundEvents.decoy.draw)
    override val grenadeType: GrenadeType = GrenadeType.DECOY
}