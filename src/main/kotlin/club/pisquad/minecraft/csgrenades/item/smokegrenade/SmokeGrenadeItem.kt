package club.pisquad.minecraft.csgrenades.item.smokegrenade

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.item.core.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.item.core.GrenadeItemSoundEvents
import club.pisquad.minecraft.csgrenades.client.sound.ModSoundEvents

class SmokeGrenadeItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    override val sounds: GrenadeItemSoundEvents = GrenadeItemSoundEvents(ModSoundEvents.smokegrenade.draw)
    override val grenadeType: GrenadeType = GrenadeType.SMOKE_GRENADE
}
