pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://maven.minecraftforge.net/") }
        maven { url = uri("https://maven.parchmentmc.org") }
    }
    plugins {
        kotlin("jvm").version("2.3.20")
        kotlin("plugin.serialization").version("2.3.20")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("0.5.0")
}
