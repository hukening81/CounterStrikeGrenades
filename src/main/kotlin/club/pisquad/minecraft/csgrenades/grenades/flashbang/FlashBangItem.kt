package club.pisquad.minecraft.csgrenades.grenades.flashbang

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem

class FlashBangItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    //    override val sounds: GrenadeItemSoundEvents = GrenadeItemSoundEvents(ModSoundEvents.flashbang.draw)
    override val grenadeType: GrenadeType = GrenadeType.FLASH_BANG

}