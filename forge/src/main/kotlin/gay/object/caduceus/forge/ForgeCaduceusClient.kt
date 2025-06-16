package gay.`object`.caduceus.forge

import gay.`object`.caduceus.CaduceusClient
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT

object ForgeCaduceusClient {
    fun init(event: FMLClientSetupEvent) {
        CaduceusClient.init()
        LOADING_CONTEXT.registerExtensionPoint(ConfigScreenFactory::class.java) {
            ConfigScreenFactory { _, parent -> CaduceusClient.getConfigScreen(parent) }
        }
    }
}
