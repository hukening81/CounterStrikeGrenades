package club.pisquad.minecraft.csgrenades.item.decoy

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.item.core.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.item.core.GrenadeItemSoundEvents
import club.pisquad.minecraft.csgrenades.registry.sounds.ModSoundEvents

class DecoyGrenadeItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    override val sounds: GrenadeItemSoundEvents = GrenadeItemSoundEvents(ModSoundEvents.decoy.draw)
    override val grenadeType: GrenadeType = GrenadeType.DECOY
}
