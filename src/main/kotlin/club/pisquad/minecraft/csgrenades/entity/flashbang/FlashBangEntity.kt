package club.pisquad.minecraft.csgrenades.entity.flashbang

import club.pisquad.minecraft.csgrenades.ModLogger
import club.pisquad.minecraft.csgrenades.config.ModConfig
import club.pisquad.minecraft.csgrenades.entity.core.ActivateByFuseGrenadeEntity
import club.pisquad.minecraft.csgrenades.entity.core.GrenadeEntityDamageTypes
import club.pisquad.minecraft.csgrenades.entity.core.GrenadeEntitySoundEvents
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.getPlayersWithinMessageRange
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.network.message.flashbang.FlashbangActivatedMessage
import club.pisquad.minecraft.csgrenades.registry.damage.ModDamageTypes
import club.pisquad.minecraft.csgrenades.registry.sounds.FlashbangSoundEvents
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import java.util.*

class FlashBangEntity(pEntityType: EntityType<out FlashBangEntity>, pLevel: Level) : ActivateByFuseGrenadeEntity(
    pEntityType, pLevel, GrenadeType.FLASH_BANG,
    ModConfig.flashbang.grenadeCommonConfig.fuseTime.get().toTick().toInt(),
) {
    override val sounds = GrenadeEntitySoundEvents(
        FlashbangSoundEvents.throwSound.get(),
        FlashbangSoundEvents.bounce.get(),
    )
    override val damageTypes: GrenadeEntityDamageTypes = GrenadeEntityDamageTypes(
        ModDamageTypes.flashbang.hit,
        ModDamageTypes.flashbang.hit,
    )

    override fun activate() {
        super.activate()
        if (this.level().isClientSide) {
            //EMPTY
        } else {
            val data = generateAffectedPlayerData()
            ModLogger.debug(this, "Sending FlashbangActivatedMessage with data size:${data.size}")
            ModPacketHandler.sendMessageToPlayer(
                this.level() as ServerLevel, this.center, FlashbangActivatedMessage(
                    data
                )
            )
            this.discard()
        }
    }

    private fun generateAffectedPlayerData(): Map<UUID, FlashbangBlindEffectData> {
        val level: ServerLevel = this.level() as ServerLevel
        val players = level.getPlayersWithinMessageRange(this.center)
        val result = mutableMapOf<UUID, FlashbangBlindEffectData>()
        players.forEach {
            val data = FlashbangEffectCalculator.calculate(this.center, it.eyePosition, it.lookAngle)
            if (data != null) {
                result[it.uuid] = data
            }
        }
        return result
    }

}
