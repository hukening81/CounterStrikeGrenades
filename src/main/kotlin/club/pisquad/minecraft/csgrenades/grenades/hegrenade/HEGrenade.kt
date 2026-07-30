package club.pisquad.minecraft.csgrenades.grenades.hegrenade

import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonSounds
import club.pisquad.minecraft.csgrenades.core.GrenadeProperties
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
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.registries.RegistryObject
import java.util.*

const val HE_GRENADE_RESOURCE_KEY = "hegrenade"

object HEGrenadeRegistryHelper {
    val entity = ModEntities.registerGrenadeEntity(HE_GRENADE_RESOURCE_KEY, ::HEGrenadeEntity)
    val item = ModItems.registerGrenadeItem(HE_GRENADE_RESOURCE_KEY) { HEGrenadeItem() }

    init {
        ModConfig.addSection("hegrenade", HEGrenadeConfig)
        ModPacketHandler.registerMessage(
            HEGrenadeActivatedMessage::class.java,
            HEGrenadeActivatedMessage.Companion::encoder,
            HEGrenadeActivatedMessage.Companion::decoder,
            HEGrenadeActivatedMessage.Companion::handler,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT),
        )
    }
}

object HEGrenadeProperties : GrenadeProperties {
    override val entity: RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> = HEGrenadeRegistryHelper.entity
    override val item: RegistryObject<out CounterStrikeGrenadeItem> = HEGrenadeRegistryHelper.item
    override val resourceKey: String = HE_GRENADE_RESOURCE_KEY
    override val sounds: GrenadeCommonSounds = HEGrenadeSounds
    override val damageTypes: GrenadeCommonDamageTypes = HEGrenadeDamageTypes
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
