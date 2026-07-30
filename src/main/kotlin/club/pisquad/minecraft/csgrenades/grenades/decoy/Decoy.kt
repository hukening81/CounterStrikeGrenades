package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonSounds
import club.pisquad.minecraft.csgrenades.core.GrenadeProperties
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.grenades.decoy.messages.ServerDecoyActivatedMessage
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangSounds
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import club.pisquad.minecraft.csgrenades.registry.ModEntities
import club.pisquad.minecraft.csgrenades.registry.ModItems
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.registries.RegistryObject
import java.util.*


const val DECOY_RESOURCE_KEY = "decoy"


object DecoyRegistryHelper {
    val entity = ModEntities.registerGrenadeEntity(DECOY_RESOURCE_KEY, ::DecoyGrenadeEntity)
    val item = ModItems.registerGrenadeItem(DECOY_RESOURCE_KEY) { DecoyGrenadeItem(Item.Properties().stacksTo(1)) }

    init {
        ModConfig.addSection("decoy", DecoyConfig)
        ModPacketHandler.registerMessage(
            ServerDecoyActivatedMessage::class.java,
            ServerDecoyActivatedMessage::encoder,
            ServerDecoyActivatedMessage::decoder,
            ServerDecoyActivatedMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        )
    }
}

object DecoyProperties : GrenadeProperties {
    override val entity: RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> = DecoyRegistryHelper.entity
    override val item: RegistryObject<out CounterStrikeGrenadeItem> = DecoyRegistryHelper.item
    override val resourceKey: String = DECOY_RESOURCE_KEY
    override val sounds: GrenadeCommonSounds = DecoySounds
    override val damageTypes: GrenadeCommonDamageTypes = DecoyDamageTypes
}

object DecoySounds : GrenadeCommonSounds {
    override val draw = GrenadeSoundData.createDraw("decoy.draw")
    override val `throw` = GrenadeSoundData.createThrow("decoy.throw")
    override val hitBlock = FlashbangSounds.hitBlock
    override val hitEntity: GrenadeSoundData = ModSoundEvents.HIT_ENTITY
    override val pinPull = GrenadeSoundData.create("decoy.pinpull")
    override val pinPullStart = GrenadeSoundData.create("decoy.pinpull_start")
}

object DecoyDamageTypes : GrenadeCommonDamageTypes {
    val explosion = ModDamageTypes.registerSingle("decoy/explosion")
    override val hit = ModDamageTypes.registerSingle("decoy/hit")
}