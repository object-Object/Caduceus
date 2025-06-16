@file:JvmName("CaduceusAbstractions")

package gay.`object`.caduceus

import dev.architectury.injectables.annotations.ExpectPlatform
import gay.`object`.caduceus.registry.CaduceusRegistrar

fun initRegistries(vararg registries: CaduceusRegistrar<*>) {
    for (registry in registries) {
        initRegistry(registry)
    }
}

@ExpectPlatform
fun <T : Any> initRegistry(registrar: CaduceusRegistrar<T>) {
    throw AssertionError()
}
