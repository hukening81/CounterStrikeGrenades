package club.pisquad.minecraft.csgrenades.item.flashbang

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.item.core.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.item.core.GrenadeItemSoundEvents
import club.pisquad.minecraft.csgrenades.registry.sounds.ModSoundEvents

class FlashBangItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    override val sounds: GrenadeItemSoundEvents = GrenadeItemSoundEvents(ModSoundEvents.flashbang.draw)
    override val grenadeType: GrenadeType = GrenadeType.FLASH_BANG

}
