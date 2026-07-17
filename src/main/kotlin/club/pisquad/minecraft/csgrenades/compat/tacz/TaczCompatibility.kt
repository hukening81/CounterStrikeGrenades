package club.pisquad.minecraft.csgrenades.compat.tacz

import com.tacz.guns.api.entity.IGunOperator
import com.tacz.guns.api.item.gun.AbstractGunItem
import com.tacz.guns.item.ModernKineticGunScriptAPI
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack


internal object TaczCompatibility {
    @Suppress("UnstableApiUsage")
    fun tryGetMainGun(player: Player): ItemStack? {
        val operator = IGunOperator.fromLivingEntity(player)
        val mainGun = player.inventory.items.mapNotNull {
            val item = it.item
            if (item !is AbstractGunItem) {
                return@mapNotNull null
            }
            val api = ModernKineticGunScriptAPI()
            api.shooter = player
            api.itemStack = it
            api.setDataHolder(operator.dataHolder)
            return@mapNotNull api
        }.maxByOrNull { it.gunIndex.gunData.roundsPerMinute } ?: return null

        return mainGun.itemStack
    }
}
//
//internal class FakeGunSoundInstance(soundDistance: Int, location: ResourceLocation, registryName: ResourceLocation) :
//    AbstractSoundInstance(location, SoundSource.PLAYERS, RandomSource.create(943)) {
//
//    val redirectedSound: FakeTaczSound =
//        FakeTaczSound(registryName, converter.idToFile(registryName), super.sound)
//
//    init {
//        val player = Minecraft.getInstance().player!!
//        this.volume = 0.8f * (1.0f - min(1.0f, sqrt(player.distanceToSqr(x, y, z)).toFloat() / soundDistance))
//        this.volume *= this.volume
//        this.pitch = 0.9f + player.random.nextFloat() * 0.125f
//    }
//
//    companion object{
//        private val converter = FileToIdConverter("tacz_sounds", ".ogg")
//
//        fun tryCreateFromData(data: DecoyFakeSoundProvider.Tacz): FakeGunSoundInstance?{
//            SoundPlayManager
//        }
//        internal fun hasSoundResource(minecraft: Minecraft,soundID: ResourceLocation): Boolean{
//            val exists = SOUND_RESOU
//        }
//    }
//
//    override fun getSound(): Sound {
//        this.sound = redirectedSound
//        return this.sound
//    }
//
//
//    override fun canPlaySound(): Boolean {
//        return true
//    }
//
//
//    internal class FakeTaczSound(location: ResourceLocation, path: ResourceLocation, template: Sound) :
//        Sound(
//            location.toString(),
//            template.volume,
//            template.pitch,
//            template.weight,
//            Type.FILE,
//            template.shouldStream(),
//            false,
//            template.attenuationDistance
//        ) {
//
//    }
//}