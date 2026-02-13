package club.pisquad.minecraft.csgrenades.compat.tacz

import club.pisquad.minecraft.csgrenades.entity.decoy.DecoyGunData
import com.tacz.guns.api.TimelessAPI
import com.tacz.guns.api.item.IGun
import net.minecraft.world.entity.player.Player
import net.minecraftforge.fml.ModList

object TaczGunDataProvider {

    fun findGunDataFromPlayer(player: Player): DecoyGunData? {
        if (!ModList.get().isLoaded("tacz")) return null

        return player.inventory.items.firstNotNullOfOrNull { itemStack ->
            (itemStack.item as? IGun)?.let { gun ->
                val gunId = gun.getGunId(itemStack)
                val fireMode = gun.getFireMode(itemStack)
                val rpm = gun.getRPM(itemStack)

                TimelessAPI.getCommonGunIndex(gunId).map { commonGunIndex ->
                    val gunData = commonGunIndex.gunData
                    val shootIntervalMs = gunData.getShootInterval(player, fireMode, itemStack).toInt()
                    DecoyGunData(
                        gunId = gunId.toString(),
                        fireMode = fireMode,
                        rpm = rpm,
                        shootIntervalMs = shootIntervalMs,
                    )
                }.orElse(null)
            }
        }
    }
}
