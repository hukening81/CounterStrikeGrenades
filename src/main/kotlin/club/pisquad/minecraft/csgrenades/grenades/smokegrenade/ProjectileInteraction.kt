package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.compat.CSGrenadeCompatibility
import club.pisquad.minecraft.csgrenades.compat.CSGrenadeSupportedMods
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages.ServerSmokeDisperseMessage
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.messages.SmokePatch
import club.pisquad.minecraft.csgrenades.network.ModPacketHandler
import com.tacz.guns.entity.EntityKineticBullet
import net.minecraft.client.Minecraft
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.ThrownTrident
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.EntityLeaveLevelEvent
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod


fun registerSmokeProjectileInteractionHandlers() {}

@Mod.EventBusSubscriber(modid = CounterStrikeGrenades.ID)
private object RegistryHelper {
    @SubscribeEvent
    @JvmStatic
    fun onSetup(event: ServerStartedEvent) {
        if (Minecraft.getInstance().level != null) {
            return
        }
        val eventBus = MinecraftForge.EVENT_BUS

        eventBus.register(Vanilla)
        if (CSGrenadeCompatibility.isModLoaded(CSGrenadeSupportedMods.TACZ)) {
            eventBus.register(Tacz)
        }
    }
}

private object Vanilla {
    val trackedEntities: MutableSet<Projectile> = mutableSetOf()

    fun shouldHandleEntity(entity: Projectile): Boolean {
        return entity is AbstractArrow || entity is ThrownTrident
    }

    @SubscribeEvent
    fun onProjectileSpawn(event: EntityJoinLevelEvent) {
        if (event.level.isClientSide) {
            return
        }
        if (event.entity !is Projectile) {
            return
        }
        val projectile = event.entity as Projectile
        if (this.shouldHandleEntity(projectile)) {
            this.trackedEntities.add(projectile)
        }
    }

    @SubscribeEvent
    fun onProjectileRemove(event: EntityLeaveLevelEvent) {
        if (event.level.isClientSide) {
            return
        }
        if (event.entity !is Projectile) {
            return
        }
        val projectile = event.entity as Projectile
        if (this.shouldHandleEntity(projectile)) {
            this.trackedEntities.remove(projectile)
        }

    }

    @SubscribeEvent
    fun tick(event: TickEvent.LevelTickEvent) {
        if (event.level.isClientSide) {
            return
        }

        this.trackedEntities.forEach {
            this.tickSingleProjectile(it, 0.5)
        }
    }

    fun tickSingleProjectile(entity: Projectile, size: Double) {
        val destination = entity.position().add(entity.deltaMovement)
        SmokeRegionEntity.trackedRegions.forEach { region ->
            if (region.boundingBox.contains(entity.position())
                || region.boundingBox.clip(
                    entity.position(),
                    destination
                ).isPresent()
            ) {
                val patch = SmokePatch.Projectile(entity.position(), destination, size)
                val message = ServerSmokeDisperseMessage(region.id, patch)
                ModPacketHandler.sendMessageToPlayer(region.level() as ServerLevel, region.position(), message)
            }
        }
    }
}

private object Tacz {
    private val trackedEntities: MutableSet<Projectile> = mutableSetOf()

    @SubscribeEvent
    fun onProjectileSpawn(event: EntityJoinLevelEvent) {
        if (event.level.isClientSide) {
            return
        }
        if (event.entity is EntityKineticBullet) {
            this.trackedEntities.add(event.entity as Projectile)
        }

    }

    @SubscribeEvent
    fun onProjectileRemove(event: EntityLeaveLevelEvent) {
        if (event.level.isClientSide) {
            return
        }
        if (event.entity is EntityKineticBullet) {
            this.trackedEntities.remove(event.entity)
        }
    }

    @SubscribeEvent
    fun tick(event: TickEvent.LevelTickEvent) {
        if (event.level.isClientSide) {
            return
        }

        this.trackedEntities.forEach {
            Vanilla.tickSingleProjectile(it, SmokeGrenadeConfig.bulletHoleSize.get())
        }
    }

}