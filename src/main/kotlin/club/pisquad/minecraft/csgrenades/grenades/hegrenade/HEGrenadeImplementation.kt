package club.pisquad.minecraft.csgrenades.grenades.hegrenade

import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonSounds
import club.pisquad.minecraft.csgrenades.core.GrenadeImplementation
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.core.sound.DistanceSegmentedSoundData
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.messages.HEGrenadeActivatedMessage
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

object HEGrenadeImplementation :
    GrenadeImplementation {
    override val resourceKey: String = "hegrenade"

    lateinit var entity: RegistryObject<EntityType<HEGrenadeEntity>>
    lateinit var item: RegistryObject<HEGrenadeItem>

    override fun getCommonSounds(): GrenadeCommonSounds {
        return HEGrenadeSounds
    }

    override fun getCommonDamageTypes(): GrenadeCommonDamageTypes {
        return HEGrenadeDamageTypes
    }

    override fun getEntity(): RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> {
        return entity
    }

    override fun getItem(): RegistryObject<out CounterStrikeGrenadeItem> {
        return item
    }

    override fun buildConfig(builder: ForgeConfigSpec.Builder) {
        HEGrenadeConfig.build(builder)
    }

    override fun registerItems(modItems: ModItems) {
        modItems.registerGrenadeItem(resourceKey) { HEGrenadeItem(Item.Properties().stacksTo(1)) }
    }

    override fun registerEntities(modEntities: ModEntities) {
        modEntities.registerGrenadeEntity(resourceKey, ::HEGrenadeEntity)
    }

    override fun registerNetworkMessages(modPacketHandler: ModPacketHandler) {
        modPacketHandler.registerMessage(
            HEGrenadeActivatedMessage::class.java,
            HEGrenadeActivatedMessage.Companion::encoder,
            HEGrenadeActivatedMessage.Companion::decoder,
            HEGrenadeActivatedMessage.Companion::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
    }

    override fun registerEntityDataSerializers() {
    }
}

object HEGrenadeDamageTypes : GrenadeCommonDamageTypes {
    override val hit = ModDamageTypes.registerSingle("hegrenade/hit")

    val explosion = ModDamageTypes.registerSingle("hegrenade/explosion")
}

object HEGrenadeSounds : GrenadeCommonSounds {
    override val draw = GrenadeSoundData.createDraw("hegrenade.draw")
    override val hitBlock = GrenadeSoundData.createHitBlock("hegrenade.hit_block")
    override val hitEntity: GrenadeSoundData = ModSoundEvents.HIT_ENTITY

    override val `throw` = GrenadeSoundData.createThrow("hegrenade.throw")
    override val pinPull = GrenadeSoundData.create("hegrenade.pinpull")
    override val pinPullStart = GrenadeSoundData.create("hegrenade.pinpull_start")

    val explode = DistanceSegmentedSoundData.createTwoPhasedExplosion(
        GrenadeSoundData.create("hegrenade.explode"),
        GrenadeSoundData.create("hegrenade.explode_distant")
    )
}