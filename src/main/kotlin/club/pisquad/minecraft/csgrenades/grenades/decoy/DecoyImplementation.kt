package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonSounds
import club.pisquad.minecraft.csgrenades.core.GrenadeImplementation
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.grenades.decoy.messages.ServerDecoyActivatedMessage
import club.pisquad.minecraft.csgrenades.grenades.flashbang.FlashbangImplementation
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

object DecoyImplementation :
    GrenadeImplementation {

    override val resourceKey: String = "decoy"

    lateinit var entity: RegistryObject<EntityType<DecoyGrenadeEntity>>
    lateinit var item: RegistryObject<DecoyGrenadeItem>

    override fun getCommonSounds(): GrenadeCommonSounds {
        return DecoySounds
    }

    override fun getCommonDamageTypes(): GrenadeCommonDamageTypes {
        return DecoyDamageTypes
    }

    override fun getEntity(): RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> {
        return entity
    }

    override fun getItem(): RegistryObject<out CounterStrikeGrenadeItem> {
        return item
    }

    override fun buildConfig(builder: ForgeConfigSpec.Builder) {
        DecoyConfig.build(builder)
    }

    override fun registerItems(modItems: ModItems) {
        modItems.registerGrenadeItem(GrenadeType.DECOY.resourceKey, { DecoyGrenadeItem(Item.Properties().stacksTo(1)) })
    }


    override fun registerEntities(modEntities: ModEntities) {
        modEntities.registerGrenadeEntity(resourceKey, ::DecoyGrenadeEntity)
    }

    override fun registerNetworkMessages(modPacketHandler: ModPacketHandler) {
        modPacketHandler.registerMessage(
            ServerDecoyActivatedMessage::class.java,
            ServerDecoyActivatedMessage::encoder,
            ServerDecoyActivatedMessage::decoder,
            ServerDecoyActivatedMessage::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        )
    }

    override fun registerEntityDataSerializers() {
    }
}

object DecoyDamageTypes : GrenadeCommonDamageTypes {
    val explosion = ModDamageTypes.registerSingle("decoy/explosion")
    override val hit = ModDamageTypes.registerSingle("decoy/hit")
}

object DecoySounds : GrenadeCommonSounds {
    override val draw = GrenadeSoundData.createDraw("decoy.draw")
    override val `throw` = GrenadeSoundData.createThrow("decoy.throw")
    override val hitBlock = FlashbangImplementation.getCommonSounds().hitBlock
    override val hitEntity: GrenadeSoundData = ModSoundEvents.HIT_ENTITY
    override val pinPull = GrenadeSoundData.create("decoy.pinpull")
    override val pinPullStart = GrenadeSoundData.create("decoy.pinpull_start")
}
