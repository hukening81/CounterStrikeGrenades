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