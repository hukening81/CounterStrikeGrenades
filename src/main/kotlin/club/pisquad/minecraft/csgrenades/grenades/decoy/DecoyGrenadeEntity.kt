package club.pisquad.minecraft.csgrenades.grenades.decoy

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.entity.impl.ActivateAfterLandingGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.entity.runOnServer
import club.pisquad.minecraft.csgrenades.grenades.decoy.messages.ServerDecoyActivatedMessage
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.physics.GrenadeDuration
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class DecoyGrenadeEntity(
    pEntityType: EntityType<out DecoyGrenadeEntity>,
    pLevel: Level,
) : ActivateAfterLandingGrenadeEntity(
    pEntityType,
    pLevel,
    GrenadeDuration.convertSecondToWholeTick(DecoyConfig.common.fuseTime.get())
) {
    override val sounds = DecoyRegistries.sounds
    override val damageTypes = DecoyRegistries.damageTypes
    override val grenadeType: GrenadeType = GrenadeType.DECOY

    override fun activate() {
        super.activate()
        this.runOnServer {
            val owner = this.level().getPlayerByUUID(this.ownerUuid) ?: return@runOnServer
            val provider = DecoyFakeSoundProvider.createProvider(this, owner as ServerPlayer)
            val message = ServerDecoyActivatedMessage(this.id, provider)

            ModPacketHandler.sendMessageToPlayer(this.level() as ServerLevel, this.position(), message)
        }
    }
}