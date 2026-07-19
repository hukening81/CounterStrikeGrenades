package club.pisquad.minecraft.csgrenades.grenades.hegrenade

import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.core.entity.impl.ActivateByFuseGrenadeEntity
import club.pisquad.minecraft.csgrenades.core.entity.runOnServer
import club.pisquad.minecraft.csgrenades.grenades.hegrenade.messages.HEGrenadeActivatedMessage
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import club.pisquad.minecraft.csgrenades.toTick
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class HEGrenadeEntity(pEntityType: EntityType<out HEGrenadeEntity>, pLevel: Level) :
    ActivateByFuseGrenadeEntity(
        pEntityType,
        pLevel,
        HEGrenadeConfig.common.fuseTime.get().toTick().toInt(),
    ) {
    override val sounds = HEGrenadeSounds
    override val damageTypes = HEGrenadeDamageTypes
    override val grenadeType: GrenadeType = GrenadeType.HE_GRENADE


    override fun activate() {
        super.activate()
        this.runOnServer {
            val center = this.grenadePosition.center
            ModPacketHandler.sendMessageToPlayer(
                this.level() as ServerLevel,
                center,
                HEGrenadeActivatedMessage(this.ownerUuid, this.center)
            )
            HEGrenadeHelper.dealDamage(this)
            this.discard()
        }
    }
}