package gay.`object`.caduceus.fabric

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import gay.`object`.caduceus.CaduceusClient

object FabricCaduceusModMenu : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory(CaduceusClient::getConfigScreen)
}
