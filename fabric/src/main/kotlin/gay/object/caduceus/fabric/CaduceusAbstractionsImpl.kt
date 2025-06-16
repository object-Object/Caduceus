@file:JvmName("CaduceusAbstractionsImpl")

package gay.`object`.caduceus.fabric

import gay.`object`.caduceus.registry.CaduceusRegistrar
import net.minecraft.core.Registry

fun <T : Any> initRegistry(registrar: CaduceusRegistrar<T>) {
    val registry = registrar.registry
    registrar.init { id, value -> Registry.register(registry, id, value) }
}
