package club.pisquad.minecraft.csgrenades.grenades.firegrenade.molotov

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

object MolotovImplementation :
    GrenadeImplementation {
    override val resourceKey: String = "molotov"

    lateinit var entity: RegistryObject<EntityType<MolotovEntity>>
    lateinit var item: RegistryObject<MolotovItem>

    override fun getCommonSounds(): GrenadeCommonSounds {
        return MolotovSounds
    }

    override fun getCommonDamageTypes(): GrenadeCommonDamageTypes {
        return MolotovDamageTypes
    }

    override fun getEntity(): RegistryObject<out EntityType<out CounterStrikeGrenadeEntity>> {
        return entity
    }

    override fun getItem(): RegistryObject<out CounterStrikeGrenadeItem> {
        return item
    }

    override fun buildConfig(builder: ForgeConfigSpec.Builder) {
        MolotovConfig.build(builder)
    }

    override fun registerItems(modItems: ModItems) {
        item = modItems.registerGrenadeItem(resourceKey) { MolotovItem(Item.Properties().stacksTo(1)) }
    }

    override fun registerEntities(modEntities: ModEntities) {
        entity = modEntities.registerGrenadeEntity(resourceKey, ::MolotovEntity)
    }

    override fun registerNetworkMessages(modPacketHandler: ModPacketHandler) {
    }

    override fun registerEntityDataSerializers() {
    }
}

object MolotovDamageTypes : GrenadeCommonDamageTypes {
    val fire = ModDamageTypes.registerSingle("molotov/fire")
    override val hit = ModDamageTypes.registerSingle("molotov/hit")
}

object MolotovSounds : GrenadeCommonSounds {
    override val draw = GrenadeSoundData.create("molotov.draw")
    override val `throw` = GrenadeSoundData.create("molotov.throw")
    override val hitBlock = GrenadeSoundData.create("molotov.bounce")
    override val hitEntity: GrenadeSoundData = ModSoundEvents.HIT_ENTITY
    override val pinPullStart = GrenadeSoundData.empty()

    val extinguish = GrenadeSoundData.create("molotov.extinguish")
    val fireIdle = GrenadeSoundData.create("molotov.fire_idle")
    override val pinPull = fireIdle
    val ignite = GrenadeSoundData.create("molotov.ignite")
    val fireLoop = GrenadeSoundData.create("molotov.fire_loop")
    val fireFadeout = GrenadeSoundData.create("molotov.fire_fadeout")
    val smash = GrenadeSoundData.create("molotov.smash")
    val detonate = GrenadeSoundData.create("molotov.detonate")
    val detonateDistant = GrenadeSoundData.create("molotov.detonate_distant")
    val detonateAir = GrenadeSoundData.create("molotov.detonate_air")
}