package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.registery.ModItems
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class MolotovEntity(pEntityType: EntityType<out ThrowableItemProjectile>, pLevel: Level) :
    AbstractFireGrenade(pEntityType, pLevel, GrenadeType.MOLOTOV) {
    override fun getDefaultItem(): Item {
        return ModItems.MOLOTOV_ITEM.get()
    }
}