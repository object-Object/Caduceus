package gay.`object`.caduceus

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

    @JvmStatic
    fun init() {
        LOGGER.info("Making Iris' Gambit more confusing...")
        initRegistries(
            CaduceusActions,
            CaduceusArithmetics,
        )
    }
}
