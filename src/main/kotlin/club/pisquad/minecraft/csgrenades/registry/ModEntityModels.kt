package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.enums.GrenadeType.DECOY
import club.pisquad.minecraft.csgrenades.enums.GrenadeType.FLASH_BANG
import club.pisquad.minecraft.csgrenades.enums.GrenadeType.HE_GRENADE
import club.pisquad.minecraft.csgrenades.enums.GrenadeType.INCENDIARY
import club.pisquad.minecraft.csgrenades.enums.GrenadeType.MOLOTOV
import club.pisquad.minecraft.csgrenades.enums.GrenadeType.SMOKE_GRENADE
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModEntityModels {
    private val hegrenadeEntityModel = create(HE_GRENADE.resourceKey)
    private val flashbangEntityModel = create(FLASH_BANG.resourceKey)
    private val smokeGrenadeEntityModel = create(SMOKE_GRENADE.resourceKey)
    private val decoyEntityModel = create(DECOY.resourceKey)
    private val molotovEntityModel = create(MOLOTOV.resourceKey)
    private val incendiaryEntityModel = create(INCENDIARY.resourceKey)

    private fun create(key: String): ResourceLocation {
        return ResourceLocation(CounterStrikeGrenades.ID, "models/entity/$key")
    }

    @JvmStatic
    @SubscribeEvent
    fun onRegisterModel(event: ModelEvent.RegisterAdditional) {
        event.register(hegrenadeEntityModel)
        event.register(flashbangEntityModel)
        event.register(smokeGrenadeEntityModel)
        event.register(decoyEntityModel)
        event.register(molotovEntityModel)
        event.register(incendiaryEntityModel)
    }

    fun getModel(grenadeType: GrenadeType): BakedModel {
        val resourceLocation = when (grenadeType) {
            FLASH_BANG -> flashbangEntityModel
            SMOKE_GRENADE -> smokeGrenadeEntityModel
            HE_GRENADE -> hegrenadeEntityModel
            INCENDIARY -> incendiaryEntityModel
            MOLOTOV -> molotovEntityModel
            DECOY -> decoyEntityModel
        }
        return Minecraft.getInstance().modelManager.getModel(resourceLocation)
    }
}
