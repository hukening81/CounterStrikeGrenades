package club.pisquad.minecraft.csgrenades.item.molotov

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.item.core.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.item.core.GrenadeItemSoundEvents
import club.pisquad.minecraft.csgrenades.registry.sounds.ModSoundEvents

class MolotovItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    override val sounds: GrenadeItemSoundEvents = GrenadeItemSoundEvents(ModSoundEvents.molotov.draw.get())
    override val grenadeType: GrenadeType = GrenadeType.MOLOTOV
}
