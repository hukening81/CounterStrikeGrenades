package club.pisquad.minecraft.csgrenades.item.core

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

private var drawSoundPlayedSlot: Int = -1

abstract class CounterStrikeGrenadeItem(properties: Properties) : Item(properties.stacksTo(2)) {
    val defaultModifiers: ImmutableMultimap<Attribute, AttributeModifier>
    abstract val sounds: GrenadeItemSoundEvents
    abstract val grenadeType: GrenadeType

    init {
        val builder = ImmutableMultimap.builder<Attribute, AttributeModifier>()
        builder.put(Attributes.ATTACK_SPEED, AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", 1000.0, AttributeModifier.Operation.ADDITION))
        this.defaultModifiers = builder.build()
    }

    override fun getAttributeModifiers(
        slot: EquipmentSlot,
        stack: ItemStack,
    ): Multimap<Attribute, AttributeModifier> = if (slot == EquipmentSlot.MAINHAND) {
        this.defaultModifiers
    } else {
        super.getDefaultAttributeModifiers(slot)
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (!level.isClientSide) return
        if (entity !is Player) return
        if (isSelected && drawSoundPlayedSlot != slotId) {
//            entity.playSound(drawSound, 0.2f, 1.0f)
            drawSoundPlayedSlot = slotId
        }
        if (entity.inventory.selected != drawSoundPlayedSlot) {
            drawSoundPlayedSlot = -1
        }
    }

    override fun onEntitySwing(stack: ItemStack?, entity: LivingEntity?): Boolean = true

    override fun onLeftClickEntity(stack: ItemStack?, player: Player?, entity: Entity?): Boolean = true

    override fun canAttackBlock(pState: BlockState, pLevel: Level, pPos: BlockPos, pPlayer: Player): Boolean = false
}
