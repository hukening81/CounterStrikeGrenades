package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.GrenadeType.*
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

    object Textures {
        private val hegrenade = createTexture(HE_GRENADE.resourceKey)
        private val flashbang = createTexture(FLASH_BANG.resourceKey)
        private val smokegrenade = createTexture(SMOKE_GRENADE.resourceKey)
        private val molotov = createTexture(MOLOTOV.resourceKey)
        private val incendiary = createTexture(INCENDIARY.resourceKey)
        private val decoy = createTexture(DECOY.resourceKey)

        private fun createTexture(key: String): ResourceLocation {
            return ResourceLocation(CounterStrikeGrenades.ID, "textures/item/${key}_3d")
        }

        fun getTexture(grenadeType: GrenadeType): ResourceLocation {
            return when (grenadeType) {
                FLASH_BANG -> this.flashbang
                SMOKE_GRENADE -> this.smokegrenade
                HE_GRENADE -> this.hegrenade
                INCENDIARY -> this.incendiary
                MOLOTOV -> this.molotov
                DECOY -> this.decoy
            }
        }
    }

    private fun create(key: String): ResourceLocation {
        return ResourceLocation(CounterStrikeGrenades.ID, "models/entity/${key}_3d.png")
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
