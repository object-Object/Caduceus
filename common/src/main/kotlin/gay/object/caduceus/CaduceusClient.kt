package gay.`object`.caduceus

import gay.`object`.caduceus.config.CaduceusConfig
import gay.`object`.caduceus.config.CaduceusConfig.GlobalConfig
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.client.gui.screens.Screen

object CaduceusClient {
    fun init() {
        CaduceusConfig.initClient()
    }

    fun getConfigScreen(parent: Screen): Screen {
        return AutoConfig.getConfigScreen(GlobalConfig::class.java, parent).get()
    }
}
