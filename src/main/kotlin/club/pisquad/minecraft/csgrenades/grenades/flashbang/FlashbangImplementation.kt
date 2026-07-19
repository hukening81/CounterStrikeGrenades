package club.pisquad.minecraft.csgrenades.grenades.flashbang

import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonSounds
import club.pisquad.minecraft.csgrenades.core.GrenadeImplementation
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
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.registries.RegistryObject
import java.util.Optional


object FlashbangImplementation :
    GrenadeImplementation {
    override val resourceKey: String = "flashbang"

    lateinit var entity: RegistryObject<EntityType<FlashBangEntity>>
    lateinit var item: RegistryObject<FlashBangItem>

    override fun getCommonSounds(): GrenadeCommonSounds {
        return FlashbangSounds
    }

    override fun getCommonDamageTypes(): GrenadeCommonDamageTypes {
        return FlashBangDamageTypes
    }

    override fun getEntity(): RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> {
        return entity
    }

    override fun getItem(): RegistryObject<out CounterStrikeGrenadeItem> {
        return item
    }

    override fun buildConfig(builder: ForgeConfigSpec.Builder) {
        FlashBangConfig.build(builder)
    }

    override fun registerItems(modItems: ModItems) {
        item = modItems.registerGrenadeItem(resourceKey) { FlashBangItem(Item.Properties().stacksTo(2)) }
    }

    override fun registerEntities(modEntities: ModEntities) {
        entity = modEntities.registerGrenadeEntity(resourceKey, ::FlashBangEntity)
    }

    override fun registerNetworkMessages(modPacketHandler: ModPacketHandler) {
        modPacketHandler.registerMessage(
            FlashbangActivatedMessage::class.java,
            FlashbangActivatedMessage::encoder,
            FlashbangActivatedMessage::decoder,
            FlashbangActivatedMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
    }

    override fun registerEntityDataSerializers() {
    }
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