plugins {
    id("caduceus.minecraft")
}

architectury {
    common("fabric", "forge")
}

dependencies {
    api(libs.clojure)

    // hex uses kotlin, so we need its types too
    api(libs.kotlin.stdlib)

    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation(libs.fabric.loader)
    modApi(libs.architectury)

    modApi(libs.hexcasting.common)

    libs.mixinExtras.common.also {
        implementation(it)
        annotationProcessor(it)
    }

    implementation(libs.classgraph)
}
