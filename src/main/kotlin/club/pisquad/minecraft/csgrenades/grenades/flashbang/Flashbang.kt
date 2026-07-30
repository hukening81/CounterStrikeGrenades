package club.pisquad.minecraft.csgrenades.grenades.flashbang

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonSounds
import club.pisquad.minecraft.csgrenades.core.GrenadeProperties
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.core.sound.DistanceSegmentedSoundData
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.grenades.flashbang.messages.FlashbangActivatedMessage
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import club.pisquad.minecraft.csgrenades.registry.ModEntities
import club.pisquad.minecraft.csgrenades.registry.ModItems
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.registries.RegistryObject
import java.util.Optional

const val FLASHBANG_RESOURCE_KEY = "flashbang"

object FlashbangRegistryHelper {
    val entity = ModEntities.registerGrenadeEntity(FLASHBANG_RESOURCE_KEY, ::FlashBangEntity)
    val item = ModItems.registerGrenadeItem(FLASHBANG_RESOURCE_KEY) { FlashBangItem(Item.Properties().stacksTo(2)) }

    init {
        ModConfig.addSection("flashbang", FlashBangConfig)
        ModPacketHandler.registerMessage(
            FlashbangActivatedMessage::class.java,
            FlashbangActivatedMessage::encoder,
            FlashbangActivatedMessage::decoder,
            FlashbangActivatedMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
    }
}

object FlashbangProperties : GrenadeProperties {
    override val entity: RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> = FlashbangRegistryHelper.entity
    override val item: RegistryObject<out CounterStrikeGrenadeItem> = FlashbangRegistryHelper.item
    override val resourceKey: String = FLASHBANG_RESOURCE_KEY
    override val sounds: GrenadeCommonSounds = FlashbangSounds
    override val damageTypes: GrenadeCommonDamageTypes = FlashBangDamageTypes
}

object FlashBangDamageTypes : GrenadeCommonDamageTypes {
    override val hit = ModDamageTypes.registerSingle("flashbang/hit")
}

object FlashbangSounds : GrenadeCommonSounds {
    override val draw = GrenadeSoundData.createDraw("flashbang.draw")
    override val hitBlock = GrenadeSoundData.createHitBlock("flashbang.hit_block")
    override val hitEntity: GrenadeSoundData = ModSoundEvents.HIT_ENTITY
    override val `throw` = GrenadeSoundData.createThrow("flashbang.throw")
    override val pinPull = GrenadeSoundData.create("flashbang.pinpull")
    override val pinPullStart = GrenadeSoundData.create("flashbang.pinpull_start")

    val ring = GrenadeSoundData.create("flashbang.ring")
    val ringLoop = GrenadeSoundData.create("flashbang.ring_loop")

    val explode = DistanceSegmentedSoundData.createTwoPhasedExplosion(
        GrenadeSoundData.create("flashbang.explode"),
        GrenadeSoundData.create("flashbang.explode_distant")
    )
}
