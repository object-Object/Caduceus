package gay.`object`.caduceus.fabric

import gay.`object`.caduceus.Caduceus
import net.fabricmc.api.ModInitializer

object FabricCaduceus : ModInitializer {
    override fun onInitialize() {
        Caduceus.init()
    }
}
