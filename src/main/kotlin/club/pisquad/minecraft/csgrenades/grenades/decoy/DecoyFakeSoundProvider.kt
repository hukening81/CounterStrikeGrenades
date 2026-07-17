package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.compat.tacz.TaczCompatibility
import club.pisquad.minecraft.csgrenades.grenades.decoy.messages.ServerDecoyActivatedMessage
import club.pisquad.minecraft.csgrenades.network.serializer.ResourceLocationSerializer
import com.tacz.guns.api.GunProperties
import com.tacz.guns.api.TimelessAPI
import com.tacz.guns.api.entity.IGunOperator
import com.tacz.guns.api.item.gun.FireMode
import com.tacz.guns.client.sound.SoundPlayManager
import com.tacz.guns.item.ModernKineticGunScriptAPI
import com.tacz.guns.network.message.ServerMessageSound
import com.tacz.guns.resource.modifier.custom.SilenceModifier
import com.tacz.guns.sound.SoundManager
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import kotlin.jvm.optionals.getOrNull
import kotlin.random.Random

class DecoyFirePatternTicker(pattern: List<Int>) {
    val pattern: MutableList<Int> = pattern.toMutableList()

    var waitingTimer: Int = 0

    fun tick(): Int? {
        if (waitingTimer > 0) {
            return 0
        }

        var count = 1

        while (waitingTimer != 0) {
            val i = pattern.removeFirstOrNull() ?: return null
            if (i == 0) {
                count++
            } else {
                waitingTimer = i
            }
        }
        return count
    }
}

interface DecoyFakeSoundPlayer {
    fun tick(): Boolean
}

@Serializable
sealed interface DecoyFakeSoundProvider {

    fun getAudioPlayer(): DecoyFakeSoundPlayer

    companion object {
        fun createProvider(decoy: DecoyGrenadeEntity, player: ServerPlayer): DecoyFakeSoundProvider {
//            if (CSGrenadeCompatibility.isModLoaded(CSGrenadeSupportedMods.TACZ)) {
//                Tacz.tryCreate(decoy, player)?.run {
//                    return this
//                }
//            }
//            return
            return Tacz.tryCreate(decoy, player)!!
        }
    }


    @Serializable
    class Vanilla : DecoyFakeSoundProvider {
        override fun getAudioPlayer(): DecoyFakeSoundPlayer {
            TODO("Not yet implemented")
        }
    }

    @Serializable
    class Tacz(
        val decoyID: Int,
        val soundDistance: Int,
        @Serializable(with = ResourceLocationSerializer::class) val gunDisplayID: ResourceLocation,
        @Serializable(with = ResourceLocationSerializer::class) val gunID: ResourceLocation,
        @Serializable(with = ResourceLocationSerializer::class) val soundID: ResourceLocation,
        val soundName: String,
        val pattern: List<Int>
    ) : DecoyFakeSoundProvider {
        companion object {
            fun tryCreate(decoy: DecoyGrenadeEntity, player: ServerPlayer): Tacz? {
                val itemStack = TaczCompatibility.tryGetMainGun(player) ?: return null
                val operator = IGunOperator.fromLivingEntity(player)
                val cacheProperty = operator.cacheProperty ?: return null
                val dataHolder = operator.dataHolder
                val api = ModernKineticGunScriptAPI()
                api.shooter = player
                api.itemStack = itemStack
                api.setDataHolder(dataHolder)

                val silence: it.unimi.dsi.fastutil.Pair<Int, Boolean> = cacheProperty.getCache(SilenceModifier.ID)

                @Suppress("UnstableApiUsage")
                val soundDistance: Int = api.abstractGunItem.modifyProperty(
                    dataHolder,
                    itemStack,
                    player,
                    GunProperties.RuntimeOnly.SOUND_DISTANCE,
                    Int.javaClass,
                    silence.first()
                ) as Int
                val useSilenceSound = silence.second()

                val soundName = if (useSilenceSound) {
                    SoundManager.SILENCE_3P_SOUND
                } else {
                    SoundManager.SHOOT_3P_SOUND
                }

                val gunDisplayID = api.abstractGunItem.getGunDisplayId(api.itemStack)
                val gunID = api.abstractGunItem.getGunId(api.itemStack)
                val gunData = api.gunIndex.gunData

                val gunDisplay = TimelessAPI.getGunDisplay(itemStack).getOrNull() ?: return null
                val soundID = gunDisplay.getSounds(soundName) ?: return null

                val fireMode = api.abstractGunItem.getFireMode(api.itemStack)
                val pattern = if (fireMode == FireMode.BURST) {
                    val interval = gunData.burstShootInterval / 1000.0
                    val burstBulletCount = gunData.burstData.count
                    DecoyHelper.generateFirePattern(
                        DecoyConfig.soundDuration.get(),
                        gunData.burstShootInterval / 1000.0,
                        DecoyConfig.soundMinGroupInterval.get() + interval,
                        DecoyConfig.soundMaxGroupInterval.get() + interval,
                        IntRange(burstBulletCount, burstBulletCount),
                        gunData.reloadData.cooldown.emptyTime.toDouble(),
                        0.0,
                        gunData.ammoAmount
                    )
                } else {
                    @Suppress("UnstableApiUsage")
                    val interval = DecoyHelper.rpmToSecondDelay(gunData.roundsPerMinute)
                    DecoyHelper.generateFirePattern(
                        DecoyConfig.soundDuration.get(),
                        interval,
                        DecoyConfig.soundMinGroupInterval.get() + interval,
                        DecoyConfig.soundMaxGroupInterval.get() + interval,
                        3..8,
                        gunData.reloadData.cooldown.emptyTime.toDouble(),
                        0.0,
                        gunData.ammoAmount
                    )
                }

                return Tacz(decoy.id, soundDistance, gunDisplayID, gunID, soundID, soundName, pattern)
            }
        }

        override fun getAudioPlayer(): DecoyFakeSoundPlayer {
            return FakeSoundPlayer(this)
        }

        private class FakeSoundPlayer(
            val data: Tacz
        ) : DecoyFakeSoundPlayer {
            val ticker = DecoyFirePatternTicker(data.pattern)

            override fun tick(): Boolean {
                val count = ticker.tick() ?: return false
                val decoy = Minecraft.getInstance().player!!.level().getEntity(this.data.decoyID) ?: return true
                val message = ServerMessageSound(
                    decoy.id,
                    data.gunID,
                    data.soundName,
                    0.8f,
                    0.9f + Random.nextFloat() * 0.125f,
                    data.soundDistance
                )
                repeat(count) {
                    SoundPlayManager.playClientSound(
                        decoy,
                        data.soundID,
                        0.8f,
                        0.9f + Random.nextFloat() * 0.125f,
                        data.soundDistance,
                        true,
                    )
                }
                return true
            }
        }
    }
}

@Mod.EventBusSubscriber(Dist.CLIENT)
object DecoyClientSoundManager {
    val activeAudioPlayers: MutableList<DecoyFakeSoundPlayer> = mutableListOf()

    fun playSoundFromMessage(message: ServerDecoyActivatedMessage) {
        activeAudioPlayers.add(message.provider.getAudioPlayer())
    }

    @JvmStatic
    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) {
            return
        }
        activeAudioPlayers.removeIf { !it.tick() }
    }
}