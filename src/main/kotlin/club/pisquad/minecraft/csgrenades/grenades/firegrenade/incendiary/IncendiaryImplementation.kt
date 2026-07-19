package club.pisquad.minecraft.csgrenades.grenades.firegrenade.incendiary

import club.pisquad.minecraft.csgrenades.core.GrenadeCommonDamageTypes
import club.pisquad.minecraft.csgrenades.core.GrenadeCommonSounds
import club.pisquad.minecraft.csgrenades.core.GrenadeImplementation
import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.item.CounterStrikeGrenadeItem
import club.pisquad.minecraft.csgrenades.core.sound.GrenadeSoundData
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.registry.ModDamageTypes
import club.pisquad.minecraft.csgrenades.registry.ModEntities
import club.pisquad.minecraft.csgrenades.registry.ModItems
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.registries.RegistryObject


object IncendiaryImplementation :
    GrenadeImplementation {
    override val resourceKey: String = "incendiary"

    lateinit var entity: RegistryObject<EntityType<IncendiaryEntity>>
    lateinit var item: RegistryObject<IncendiaryItem>

    override fun getCommonSounds(): GrenadeCommonSounds {
        return IncendiarySounds
    }

    override fun getCommonDamageTypes(): GrenadeCommonDamageTypes {
        return IncendiaryDamageTypes
    }

    override fun getEntity(): RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> {
        return entity
    }

    override fun getItem(): RegistryObject<out CounterStrikeGrenadeItem> {
        return item
    }

    override fun buildConfig(builder: ForgeConfigSpec.Builder) {
        IncendiaryConfig.build(builder)
    }

    override fun registerItems(modItems: ModItems) {
        item = modItems.registerGrenadeItem(resourceKey) { IncendiaryItem(Item.Properties().stacksTo(1)) }
    }

    override fun registerEntities(modEntities: ModEntities) {
        entity = modEntities.registerGrenadeEntity(resourceKey, ::IncendiaryEntity)
    }

    override fun registerNetworkMessages(modPacketHandler: ModPacketHandler) {

    }

    override fun registerEntityDataSerializers() {
    }
}

object IncendiaryDamageTypes : GrenadeCommonDamageTypes {
    val fire = ModDamageTypes.registerSingle("incendiary/fire")
    override val hit = ModDamageTypes.registerSingle("incendiary/hit")
}

object IncendiarySounds : GrenadeCommonSounds {
    override val hitBlock = GrenadeSoundData.create("incendiary.hit_block")
    override val hitEntity: GrenadeSoundData = ModSoundEvents.HIT_ENTITY
    override val draw = GrenadeSoundData.create("incendiary.draw")
    override val pinPull = GrenadeSoundData.create("incendiary.pinpull")
    override val pinPullStart = GrenadeSoundData.create("incendiary.pinpull_start")
    override val `throw` = GrenadeSoundData.create("incendiary.throw")

    val detonate = GrenadeSoundData.create("incendiary.detonate")
    val pop = GrenadeSoundData.create("incendiary.pop")
    val detonateDistant = GrenadeSoundData.create("incendiary.detonate_distant")
    val detonateAir = GrenadeSoundData.create("incendiary.detonate_air")
}