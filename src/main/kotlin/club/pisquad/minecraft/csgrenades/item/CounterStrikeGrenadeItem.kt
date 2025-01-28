package club.pisquad.minecraft.csgrenades.item

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.GrenadeThrowType
import club.pisquad.minecraft.csgrenades.network.message.GrenadeThrownMessage
import net.minecraft.core.Rotations
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.time.Duration
import java.time.Instant

private var drawSoundPlayedSlot: Int = -1


open class CounterStrikeGrenadeItem(properties: Properties) : Item(properties.stacksTo(1)) {

    lateinit var grenadeType: GrenadeType

    // Sounds
    var drawSound: SoundEvent = SoundEvents.EMPTY

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

    fun throwAction(player: Player, hand: InteractionHand, throwType: GrenadeThrowType) {
        val playerSpeedFactor = when (throwType) {
            GrenadeThrowType.Strong -> STRONG_THROW_PLAYER_SPEED_FACTOR
            GrenadeThrowType.Weak -> WEAK_THROW_PLAYER_SPEED_FACTOR
        }

        val speed = player.deltaMovement.scale(playerSpeedFactor)
            .add(player.lookAngle.normalize().scale(throwType.speed))
            .length()
        val playerPos = player.position()
        CsGrenadePacketHandler.INSTANCE.sendToServer(
            GrenadeThrownMessage(
                player.uuid,
                hand,
                speed,
                grenadeType,
                Vec3(playerPos.x, playerPos.y + PLAYER_EYESIGHT_OFFSET, playerPos.z),
                Rotations(player.xRot, player.yRot, 0.0f),
            )
        )
    }
}

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object PlayerInteractEventHandler {
    private var grenadeLastThrow = Instant.now()

    @SubscribeEvent
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val itemInHand = event.entity.getItemInHand(event.hand).item
        if (itemInHand !is CounterStrikeGrenadeItem) return
        if (Duration.between(this.grenadeLastThrow, Instant.now()).toMillis() < GRENADE_THROW_COOLDOWN) return

        if (event.level.isClientSide) {
            when (event) {
                is PlayerInteractEvent.LeftClickBlock,
                is PlayerInteractEvent.LeftClickEmpty -> {
                    itemInHand.throwAction(event.entity, event.hand, GrenadeThrowType.Strong)
                }

                is PlayerInteractEvent.RightClickItem,
                is PlayerInteractEvent.RightClickBlock,
                is PlayerInteractEvent.EntityInteract -> {
                    // note that PlayerInteractEvent.EntityInteract fires when player right-clicks an entity
                    itemInHand.throwAction(event.entity, event.hand, GrenadeThrowType.Weak)
                }
            }
            if (!event.entity.isCreative) {
                event.entity.inventory.removeItem(event.itemStack)
            }
            this.grenadeLastThrow = Instant.now()
        }

    }
}