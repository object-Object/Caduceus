package gay.`object`.caduceus

import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import gay.`object`.caduceus.config.CaduceusConfig
import gay.`object`.caduceus.networking.CaduceusNetworking
import gay.`object`.caduceus.registry.CaduceusActions

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
        )
        CaduceusNetworking.init()
    }
}
