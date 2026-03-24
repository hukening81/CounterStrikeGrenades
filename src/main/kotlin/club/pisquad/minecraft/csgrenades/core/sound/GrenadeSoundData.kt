package club.pisquad.minecraft.csgrenades.core.sound

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.ModSettings
import club.pisquad.minecraft.csgrenades.registry.ModSoundEvents
import net.minecraft.client.Minecraft
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.phys.Vec3
import net.minecraftforge.registries.RegistryObject
import kotlin.random.Random


class GrenadeSoundData(
    val soundRegistry: RegistryObject<SoundEvent>,
    val volumeD: Double,
    val source: SoundSource = SoundSource.PLAYERS
) {
    val soundEvent: SoundEvent
        get() = soundRegistry.get()
    val volume: Float
        get() = volumeD.toFloat()

    companion object {
        fun create(name: String, volume: Double = 10.0): GrenadeSoundData {
            return GrenadeSoundData(
                ModSoundEvents.registerSoundEvent(name),
                volume
            )
        }

        fun createDraw(name: String): GrenadeSoundData {
            return create(name, ModSettings.Sound.Volume.DRAW)
        }

        fun createThrow(name: String): GrenadeSoundData {
            return create(name, ModSettings.Sound.Volume.THROW)
        }

        fun createHitBlock(name: String): GrenadeSoundData {
            return create(name, ModSettings.Sound.Volume.HIT_BLOCK)
        }

    }

    /**
     * Play this sound on client side
     *
     * @param position
     * @param volume: use this control to overwrite for testing
     * @return
     */
    fun play(position: Vec3, volume: Double? = volumeD): Boolean {
        val player = Minecraft.getInstance().player
        ModLogger.debug("Playing sound $soundEvent")
        Minecraft.getInstance().level
            ?.playSeededSound(
                player,
                position.x,
                position.y,
                position.z,
                soundEvent,
                source,
                volume?.toFloat() ?: this.volume,
                1.0f,
                Random.nextLong()
            ) ?: return false
        return true
    }
}

class DistanceSegmentedSoundData(vararg val ranges: Pair<Double, GrenadeSoundData>) {
    fun play(position: Vec3, distance: Double, volume: Double? = null): Boolean {
        for (section in ranges) {
            if (distance < section.first) {
                val data = section.second
                return data.play(position, volume)
            }
        }
        return false
    }

    companion object {
        fun createTwoPhasedExplosion(
            explode: GrenadeSoundData,
            explodeDistant: GrenadeSoundData
        ): DistanceSegmentedSoundData {
            return DistanceSegmentedSoundData(
                Pair(ModSettings.Sound.EXPLOSION_SOUND_CHANGE_DISTANCE, explode),
                Pair(ModSettings.SERVER_MESSAGE_RANGE, explodeDistant)
            )
        }
    }
}