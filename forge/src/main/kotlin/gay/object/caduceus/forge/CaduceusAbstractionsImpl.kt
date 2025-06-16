@file:JvmName("CaduceusAbstractionsImpl")

package gay.`object`.caduceus.forge

import gay.`object`.caduceus.registry.CaduceusRegistrar
import net.minecraftforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

fun <T : Any> initRegistry(registrar: CaduceusRegistrar<T>) {
    MOD_BUS.addListener { event: RegisterEvent ->
        event.register(registrar.registryKey) { helper ->
            registrar.init(helper::register)
        }
    }
}
