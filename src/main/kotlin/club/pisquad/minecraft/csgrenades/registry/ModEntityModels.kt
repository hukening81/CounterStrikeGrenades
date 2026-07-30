package club.pisquad.minecraft.csgrenades.registry

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.GrenadeType.*
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModEntityModels {
    private val hegrenadeEntityModel = create(HE_GRENADE.resourceKey)
    private val flashbangEntityModel = create(FLASH_BANG.resourceKey)

    //    private val smokeGrenadeEntityModel = create(SMOKE_GRENADE.resourceKey)
    private val decoyEntityModel = create(DECOY.resourceKey)
    private val molotovEntityModel = create(MOLOTOV.resourceKey)
    private val incendiaryEntityModel = create(INCENDIARY.resourceKey)

    object Textures {
        private val hegrenade = createTexture(HE_GRENADE.resourceKey)
        private val flashbang = createTexture(FLASH_BANG.resourceKey)
        private val smoke_t = createTexture(T_SMOKE.resourceKey)
        private val smoke_ct = createTexture(CT_SMOKE.resourceKey)
        private val molotov = createTexture(MOLOTOV.resourceKey)
        private val incendiary = createTexture(INCENDIARY.resourceKey)
        private val decoy = createTexture(DECOY.resourceKey)

        private fun createTexture(key: String): ResourceLocation {
            return ResourceLocation(CounterStrikeGrenades.ID, "textures/item/${key}")
        }

        fun getTexture(grenadeType: GrenadeType): ResourceLocation {
            return when (grenadeType) {
                FLASH_BANG -> this.flashbang
                HE_GRENADE -> this.hegrenade
                INCENDIARY -> this.incendiary
                MOLOTOV -> this.molotov
                DECOY -> this.decoy
                CT_SMOKE -> this.smoke_ct
                T_SMOKE -> this.smoke_t
            }
        }
    }

    private fun create(key: String): ResourceLocation {
        return ResourceLocation(CounterStrikeGrenades.ID, "models/entity/${key}.png")
    }

    //    @JvmStatic
//    @SubscribeEvent
    fun onRegisterModel(event: ModelEvent.RegisterAdditional) {
//        event.register(hegrenadeEntityModel)
//        event.register(flashbangEntityModel)
////        event.register(smokeGrenadeEntityModel)
//        event.register(decoyEntityModel)
//        event.register(molotovEntityModel)
//        event.register(incendiaryEntityModel)
    }

//    fun getModel(grenadeType: GrenadeType): BakedModel {
//        val resourceLocation = when (grenadeType) {
//            FLASH_BANG -> flashbangEntityModel
//            HE_GRENADE -> hegrenadeEntityModel
//            INCENDIARY -> incendiaryEntityModel
//            MOLOTOV -> molotovEntityModel
//            DECOY -> decoyEntityModel
//            CT_SMOKE -> TODO()
//            T_SMOKE -> TODO()
//        }
//        return Minecraft.getInstance().modelManager.getModel(resourceLocation)
//    }
}
