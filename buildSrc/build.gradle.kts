import dev.panuszewski.gradle.pluginMarker

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://maven.architectury.dev/") }
    maven { url = uri("https://maven.fabricmc.net/") }
    maven { url = uri("https://maven.minecraftforge.net/") }
    maven { url = uri("https://maven.blamejared.com/") }
}

dependencies {
    // plugins used in convention plugins
    implementation(pluginMarker(libs.plugins.kotlin.jvm))
    implementation(pluginMarker(libs.plugins.architectury.asProvider()))
    implementation(pluginMarker(libs.plugins.architectury.loom))
    implementation(pluginMarker(libs.plugins.shadow))
    implementation(pluginMarker(libs.plugins.modPublish))
    implementation(pluginMarker(libs.plugins.pkJson5))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}
