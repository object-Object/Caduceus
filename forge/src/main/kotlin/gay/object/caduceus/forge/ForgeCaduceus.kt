package gay.`object`.caduceus.forge

import dev.architectury.platform.forge.EventBuses
import gay.`object`.caduceus.Caduceus
import net.minecraft.data.DataProvider
import net.minecraft.data.DataProvider.Factory
import net.minecraft.data.PackOutput
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(Caduceus.MODID)
class CaduceusForge {
    init {
        MOD_BUS.apply {
            EventBuses.registerModEventBus(Caduceus.MODID, this)
            addListener(ForgeCaduceusClient::init)
            addListener(::gatherData)
        }
        Caduceus.init()
    }

    private fun gatherData(event: GatherDataEvent) {
        event.apply {
            // TODO: add datagen providers here
        }
    }
}

fun <T : DataProvider> GatherDataEvent.addProvider(run: Boolean, factory: (PackOutput) -> T) =
    generator.addProvider(run, Factory { factory(it) })
