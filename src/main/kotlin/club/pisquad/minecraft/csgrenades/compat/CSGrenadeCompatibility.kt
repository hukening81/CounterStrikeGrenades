package club.pisquad.minecraft.csgrenades.compat

import net.minecraftforge.fml.ModList


enum class CSGrenadeSupportedMods(val modName: String) {
    TACZ("tacz");

    fun isLoaded(): Boolean {
        return CSGrenadeCompatibility.isModLoaded(this)
    }
}

object CSGrenadeCompatibility {
    val supportedMods: List<String> = CSGrenadeSupportedMods.entries.map { it.modName }

    fun isModLoaded(mod: CSGrenadeSupportedMods): Boolean {
        return ModList.get().isLoaded(mod.modName)
    }

    fun <T> runIfLoaded(mod: CSGrenadeSupportedMods, action: Function0<T>): T? {
        return if (mod.isLoaded()) {
            action()
        } else {
            null
        }
    }
}