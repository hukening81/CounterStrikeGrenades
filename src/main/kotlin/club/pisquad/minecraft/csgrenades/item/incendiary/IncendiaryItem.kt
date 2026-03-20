package club.pisquad.minecraft.csgrenades.item.incendiary

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.item.core.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.item.core.GrenadeItemSoundEvents
import club.pisquad.minecraft.csgrenades.registry.sounds.ModSoundEvents

class IncendiaryItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    override val sounds: GrenadeItemSoundEvents = GrenadeItemSoundEvents(ModSoundEvents.incendiary.draw)
    override val grenadeType: GrenadeType = GrenadeType.INCENDIARY
}
