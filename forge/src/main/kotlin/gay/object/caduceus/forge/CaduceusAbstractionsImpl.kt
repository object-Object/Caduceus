@file:JvmName("CaduceusAbstractionsImpl")

package gay.`object`.caduceus.forge

import gay.`object`.caduceus.registry.CaduceusRegistrar
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.RegisterEvent

fun <T : Any> initRegistry(registrar: CaduceusRegistrar<T>) {
    FMLJavaModLoadingContext.get().modEventBus.addListener { event: RegisterEvent ->
        event.register(registrar.registryKey) { helper ->
            registrar.init(helper::register)
        }
    }
}
