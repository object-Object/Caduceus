package gay.`object`.caduceus

import gay.`object`.caduceus.config.CaduceusConfig
import gay.`object`.caduceus.networking.CaduceusNetworking
import gay.`object`.caduceus.registry.CaduceusActions
import gay.`object`.caduceus.registry.CaduceusArithmetics
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object Caduceus {
    const val MODID = "caduceus"

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MODID)

    @JvmStatic
    fun id(path: String) = ResourceLocation(MODID, path)

    fun init() {
        CaduceusConfig.init()
        initRegistries(
            CaduceusActions,
            CaduceusArithmetics,
        )
        CaduceusNetworking.init()
    }
}
