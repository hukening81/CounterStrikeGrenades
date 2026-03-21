package club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem

class MolotovItem(properties: Properties) : CounterStrikeGrenadeItem(properties) {
    //    override val sounds: GrenadeItemSoundEvents = GrenadeItemSoundEvents(ModSoundEvents.molotov.draw)
    override val grenadeType: GrenadeType = GrenadeType.MOLOTOV
}
