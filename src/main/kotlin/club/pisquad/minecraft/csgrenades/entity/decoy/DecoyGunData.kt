package club.pisquad.minecraft.csgrenades.entity.decoy

import com.tacz.guns.api.item.gun.FireMode

data class DecoyGunData(
    val gunId: String = "",
    val fireMode: FireMode = FireMode.SEMI,
    val rpm: Int = 0,
    val shootIntervalMs: Int = 0,
) {
    val isValid: Boolean
        get() = gunId.isNotBlank() && rpm > 0 && shootIntervalMs > 0
}
