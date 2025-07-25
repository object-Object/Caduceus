plugins {
    id("caduceus.platform")
}

val modId: String by project

architectury {
    forge()
}

loom {
    forge {
        convertAccessWideners = true
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)

        mixinConfig(
            "caduceus-common.mixins.json",
            "caduceus-forge.mixins.json",
        )
    }

    runs {
        register("commonDatagen") {
            data()
            programArgs(
                "--mod", modId,
                "--all",
                // we use forge to do the common datagen because fabric's datagen kind of sucks
                "--output", project(":common").file("src/generated/resources").absolutePath,
                "--existing", file("src/main/resources").absolutePath,
                "--existing", project(":common").file("src/main/resources").absolutePath,
            )
            property("caduceus.apply-datagen-mixin", "true")
        }
    }
}

caduceusModDependencies {
    // expand versions in mods.toml
    filesMatching.add("META-INF/mods.toml")

    // transform Gradle version ranges into a Forge-compatible format
    anyVersion = ""
    mapVersions {
        replace(Regex("""\](\S+)"""), "($1")
        replace(Regex("""(\S+)\["""), "$1)")
    }

    // CurseForge/Modrinth mod dependency metadata
    requires("architectury-api")
    requires(curseforge = "hexcasting", modrinth = "hex-casting")
}

dependencies {
    libs.bundles.clojure.also {
        api(it)
        include(it)
        forgeRuntimeLibrary(it)
    }

    // hex uses kotlin, so we need its types too
    api(libs.kotlin.stdlib)

    forge(libs.forge)
    modApi(libs.architectury.forge)

    modApi(libs.hexcasting.forge) { isTransitive = false }
    // Hex Casting dependencies
    // we use modLocalRuntime to add these to the development runtime, but not at compile time or for consumers of this project
    // but we use PAUCAL for datagen, so that's part of the actual implementation
    modImplementation(libs.paucal.forge)
    modLocalRuntime(libs.patchouli.forge)
    modLocalRuntime(libs.caelus)
    modLocalRuntime(libs.inline.forge) { isTransitive = false }
    modLocalRuntime(libs.clothConfig.forge)
    localRuntime(libs.kotlin.forge)

    libs.mixinExtras.common.also {
        compileOnly(it)
        annotationProcessor(it)
    }

    libs.mixinExtras.forge.also {
        localRuntime(it)
        include(it)
    }

    libs.classgraph.also {
        implementation(it)
        include(it)
        forgeRuntimeLibrary(it)
    }
}

tasks {
    shadowJar {
        exclude("fabric.mod.json")
    }
}
