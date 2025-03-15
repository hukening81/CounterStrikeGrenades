package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

private var drawSoundPlayedSlot: Int = -1


open class CounterStrikeGrenadeItem(properties: Properties) : Item(properties.stacksTo(1)) {

    lateinit var grenadeType: GrenadeType

    // Sounds
    var drawSound: SoundEvent = SoundEvents.EMPTY

    override fun getAttributeModifiers(
        slot: EquipmentSlot?,
        stack: ItemStack?
    ): Multimap<Attribute, AttributeModifier> {
        val modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack))
        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(
                Attributes.ATTACK_SPEED,
                AttributeModifier(
                    "Weapon attack speed",
                    ModConfig.GRENADE_THROW_COOLDOWN.get().div(50.0) - 4,
                    AttributeModifier.Operation.ADDITION
                )
            )
        }
        return modifiers
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (!level.isClientSide) return
        if (entity !is Player) return

        if (isSelected && drawSoundPlayedSlot != slotId) {
            entity.playSound(drawSound, 0.2f, 1.0f)
            drawSoundPlayedSlot = slotId
        }
        if (entity.inventory.selected != drawSoundPlayedSlot) {
            drawSoundPlayedSlot = -1
        }
    }
}