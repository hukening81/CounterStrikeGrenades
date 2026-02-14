package club.pisquad.minecraft.csgrenades.client.render.flashbang

import club.pisquad.minecraft.csgrenades.*
import club.pisquad.minecraft.csgrenades.network.message.*
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.player.Player
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.time.Instant
import java.util.*

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object FlashbangParticleEffectRenderer {
    //    value is the time we should stop rendering, in Epoch milisecond
    private val renderingPlayers = mutableMapOf<UUID, Long>()

    fun render(playerInfo: AffectedPlayerInfo) {
        if (renderingPlayers.containsKey(playerInfo.uuid)) {
//            This may not be accurate, but I will leave it be now.
            renderingPlayers[playerInfo.uuid] = Instant.now()
                .toEpochMilli() + playerInfo.effectData.effectSustain + playerInfo.effectData.effectDecay.toLong()
        } else {
            renderingPlayers[playerInfo.uuid] = Instant.now()
                .toEpochMilli() + playerInfo.effectData.effectAttack + playerInfo.effectData.effectSustain + playerInfo.effectData.effectDecay.toLong()
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun render(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            val timeNowEpoch = Instant.now().toEpochMilli()
            val level = Minecraft.getInstance().level ?: return
            renderingPlayers.filter { it.value < timeNowEpoch }.forEach { renderingPlayers.remove(it.key) }
            renderingPlayers.forEach { (uuid, _) ->
                level.getPlayerByUUID(uuid)?.let { createParticleAtPlayer(it) }
            }
        }
    }

    private fun createParticleAtPlayer(player: Player) {
        val particleEngine = Minecraft.getInstance().particleEngine
        val position = player.eyePosition.add(player.lookAngle.scale(0.6))
        val speed = player.deltaMovement

        val particle = particleEngine.createParticle(
            ParticleTypes.END_ROD,
            position.x,
            position.y,
            position.z,
            speed.x,
            speed.y,
            speed.z,
        ) ?: return
        particle.lifetime = 1
        particle.scale(3f)
    }
}
