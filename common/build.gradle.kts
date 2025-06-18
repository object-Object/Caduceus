import dev.clojurephant.plugin.clojure.tasks.ClojureCheck

plugins {
    id("caduceus.minecraft")
}

architectury {
    common("fabric", "forge")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(kotlin("reflect"))

    implementation(libs.clojure)

    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation(libs.fabric.loader)
    modApi(libs.architectury)

    modApi(libs.hexcasting.common)

    modApi(libs.clothConfig.common)

    libs.mixinExtras.common.also {
        implementation(it)
        annotationProcessor(it)
    }
}

tasks {
    // FIXME: remove when kotlin is gone
    named<ClojureCheck>("checkClojure") {
        namespaces.set(listOf())
    }
}
