// A convention plugin that should be applied to all build scripts.

package caduceus

import libs

plugins {
    java
    id("dev.clojurephant.clojure")
    id("architectury-plugin")
}

val mavenGroup: String by project
val modVersion: String by project
val javaVersion = libs.versions.java.get().toInt()
val minecraftVersion = libs.versions.minecraft.get()
val release = System.getenv("RELEASE") == "true"

group = mavenGroup

version = "$modVersion+$minecraftVersion"
if (!release) {
    version = "$version-SNAPSHOT"
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://maven.blamejared.com") }
    maven { url = uri("https://maven.fabricmc.net/") }
    maven { url = uri("https://maven.ladysnake.org/releases") } // Cardinal Components
    maven { url = uri("https://maven.minecraftforge.net/") }
    maven { url = uri("https://maven.parchmentmc.org") }
    maven { url = uri("https://maven.shedaniel.me") }
    maven { url = uri("https://maven.terraformersmc.com/releases") }
    maven { url = uri("https://maven.theillusivec4.top") } // Caelus
    maven { url = uri("https://thedarkcolour.github.io/KotlinForForge") }
    maven { url = uri("https://repo.clojars.org") } // Clojurephant
    exclusiveContent {
        filter {
            includeGroup("maven.modrinth")
        }
        forRepository {
            maven { url = uri("https://api.modrinth.com/maven") }
        }
    }
    exclusiveContent {
        filter {
            includeGroup("libs")
        }
        forRepository {
            flatDir { dir(rootProject.file("libs")) }
        }
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

clojure {
    builds {
        named("main") {
            // remove java from clojure classpath to prevent circular dependency
            classpath.setFrom(sourceSets.main.map { it.compileClasspath })

            // compile everything ahead of time so it can be remapped
            aotAll()

            compiler {
                directLinking = true

                // don't stringify classnames that need to be remapped
                elideMeta.add("tag")
            }
        }
    }
}

tasks {
    compileJava {
        // allow importing clojure classes from java
        classpath += files(sourceSets.main.map { it.clojure.classesDirectory })

        options.apply {
            encoding = "UTF-8"
            release = javaVersion
        }
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    withType<GenerateModuleMetadata>().configureEach {
        enabled = false
    }

    javadoc {
        options {
            this as StandardJavadocDocletOptions
            addStringOption("Xdoclint:none", "-quiet")
        }
    }

    processResources {
        exclude(".cache")
    }

    processTestResources {
        exclude(".cache")
    }
}
