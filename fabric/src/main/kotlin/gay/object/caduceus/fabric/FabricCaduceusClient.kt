package gay.`object`.caduceus.fabric

import gay.`object`.caduceus.CaduceusClient
import net.fabricmc.api.ClientModInitializer

object FabricCaduceusClient : ClientModInitializer {
    override fun onInitializeClient() {
        CaduceusClient.init()
    }
}
