package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.core.CSGrenadeEntityDataSerializer
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonSounds
import club.pisquad.minecraft.csgrenades.core.GrenadeImplementation
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages.SmokeGrenadeActivatedMessage
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages.SmokeGrenadeActivatedMessage.Companion.decoder
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages.SmokeGrenadeActivatedMessage.Companion.encoder
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages.SmokeGrenadeActivatedMessage.Companion.handler
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.voxel.VoxelMap
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import club.pisquad.minecraft.csgrenades.registry.ModEntities
import club.pisquad.minecraft.csgrenades.registry.ModItems
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.item.Item
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.registries.RegistryObject
import org.apache.http.client.entity.EntityBuilder
import java.util.Optional

object SmokeGrenadeImplementation : GrenadeImplementation {
    override val resourceKey: String = "smokegrenade"

    lateinit var entity: RegistryObject<EntityType<SmokeGrenadeEntity>>
    lateinit var smokeRegionEntity: RegistryObject<EntityType<SmokeRegionEntity>>
    lateinit var item: RegistryObject<SmokeGrenadeItem>

    override fun getCommonSounds(): GrenadeCommonSounds {
        return SmokeGrenadeSounds
    }

    override fun getCommonDamageTypes(): GrenadeCommonDamageTypes {
        return SmokeGrenadeDamageTypes
    }

    override fun getEntity(): RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> {
        return entity
    }

    override fun getItem(): RegistryObject<out CounterStrikeGrenadeItem> {
        return item
    }

    override fun buildConfig(builder: ForgeConfigSpec.Builder) {
        SmokeGrenadeConfig.build(builder)
    }

    override fun registerItems(modItems: ModItems) {
        item = modItems.registerGrenadeItem(resourceKey) { SmokeGrenadeItem(Item.Properties().stacksTo(1)) }
    }

    override fun registerEntities(modEntities: ModEntities) {
        entity = modEntities.registerGrenadeEntity(resourceKey, ::SmokeGrenadeEntity)
        smokeRegionEntity = modEntities.ENTITIES.register("smokeregion") {
            EntityType.Builder.of(::SmokeRegionEntity, MobCategory.MISC).sized(10f, 10f)
                .updateInterval(1)
                .build("smokeregion")
        }
    }

    override fun registerNetworkMessages(modPacketHandler: ModPacketHandler) {
        modPacketHandler.registerMessage(
            SmokeGrenadeActivatedMessage::class.java,
            SmokeGrenadeActivatedMessage.Companion::encoder,
            SmokeGrenadeActivatedMessage.Companion::decoder,
            SmokeGrenadeActivatedMessage.Companion::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
    }

    override fun registerEntityDataSerializers() {
        EntityDataSerializers.registerSerializer(SmokeGrenadeSerializers.voxelMapSerializer)
    }
}

object SmokeGrenadeDamageTypes : GrenadeCommonDamageTypes {
    override val hit = ModDamageTypes.registerSingle("smokegrenade/hit")
}

object SmokeGrenadeSounds : GrenadeCommonSounds {
    override val draw = GrenadeSoundData.create("smokegrenade.draw")
    override val hitBlock = GrenadeSoundData.create("smokegrenade.bounce")
    override val hitEntity: GrenadeSoundData = ModSoundEvents.HIT_ENTITY
    override val `throw` = GrenadeSoundData.create("smokegrenade.throw")
    override val pinPull = GrenadeSoundData.create("smokegrenade.pinpull")
    override val pinPullStart = GrenadeSoundData.create("smokegrenade.pinpull_start")

    val can = GrenadeSoundData.create("smokegrenade.can")
    val explodeDistant = GrenadeSoundData.create("smokegrenade.explode_distant")
    val clear = GrenadeSoundData.create("smokegrenade.clear")
    val emit = GrenadeSoundData.create("smokegrenade.emit")
}

object SmokeGrenadeSerializers {
    val voxelMapSerializer = CSGrenadeEntityDataSerializer(VoxelMap.serializer())
}