package club.pisquad.minecraft.csgrenades.item.hegrenade

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.item.core.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.item.core.GrenadeItemSoundEvents
import club.pisquad.minecraft.csgrenades.registry.sounds.ModSoundEvents

class HEGrenadeItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    override val sounds: GrenadeItemSoundEvents = GrenadeItemSoundEvents(ModSoundEvents.hegrenade.draw)
    override val grenadeType: GrenadeType = GrenadeType.HE_GRENADE
}
