import net.minecraftforge.gradle.userdev.UserDevExtension
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.text.SimpleDateFormat
import java.util.Date

val modVersion: String by project
val modGroupId: String by project
val modId: String by project
val mappingChannel: String by project
val configMappingChannel = mappingChannel
val mappingVersion: String by project
val configMappingVersion = mappingVersion
val minecraftVersion: String by project
val forgeVersion: String by project
val modName: String by project
val modLicense: String by project
val minecraftVersionRange: String by project
val forgeVersionRange: String by project
val modAuthors: String by project
val modDescription: String by project

val kotlinSerializationVersion = "1.10.0"

plugins {
    id("idea")
    id("maven-publish")
    id("net.minecraftforge.gradle").version("[6.0,6.2)")
    id("org.parchmentmc.librarian.forgegradle").version("1.+")
    // Adds the Kotlin Gradle plugin
//    id("org.jetbrains.kotlin.jvm").version("2.3.0")
    // OPTIONAL Kotlin Serialization plugin
//    id("org.jetbrains.kotlin.plugin.serialization").version("2.3.0")
    kotlin("plugin.serialization").version("2.3.0")
    kotlin("jvm").version("2.3.0")
    id("com.gradleup.shadow").version("9.2.0")
}

version = modVersion
group = modGroupId

base {
    archivesName = modId
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

println("Java: ${System.getProperty("java.version")}, JVM: ${System.getProperty("java.vm.version")} (${System.getProperty("java.vendor")}), Arch: ${System.getProperty("os.arch")}")
configure<UserDevExtension> {
    // Change to your preferred mappings
    mappings(configMappingChannel, configMappingVersion)
    // Add your AccessTransformer

    // accessTransformer = file("src/main/resources/META-INF/accesstransformer.cfg")

    runs {
        // REQUIRED for processResources to work in dev
        copyIdeResources = true

        create("client") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", modId)

            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "$projectDir/build/createSrgToMcp/output.srg")

            if (org.gradle.internal.os.OperatingSystem.current().isLinux) {
                // NOTE(hukening81): This is mainly for my use case, since native glfw has some issue under wayland what will crash the test instance.
                println("Running on a linux machine, use custom glfw library")
                jvmArg("-Dorg.lwjgl.glfw.libname=/usr/lib/libglfw.so")
            }
            lazyToken("minecraft_classpath") {
                configurations.getByName("shade").files.joinToString(File.pathSeparator)
            }
            mods {
                create(modId) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("server") {
            workingDirectory(project.file("run/server"))

            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", modId)

            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "$projectDir/build/createSrgToMcp/output.srg")

            mods {
                create(modId) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("gameTestServer") {
            workingDirectory(project.file("run/server"))

            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", modId)

            mods {
                create(modId) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("data") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            args("--mod", modId, "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources"))

            mods {
                create(modId) {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

// Include assets and data from data generators
sourceSets.main.get().resources { srcDirs("src/generated/resources/") }

repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
    maven {
        name = "Curse Maven"
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }

    flatDir {
        dirs("libs")
    }
}

configurations {
    val shade by creating
    implementation.get().extendsFrom(shade)
}

dependencies {
    // Use the latest version of Minecraft Forge
    minecraft("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")

    implementation(fg.deobf("curse.maven:timeless-and-classics-zero-1028108:7401617"))

    "shade"("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinSerializationVersion") {
        exclude(group = "org.jetbrains", module = "annotations")
    }

    "shade"("org.jetbrains.kotlinx:kotlinx-serialization-cbor-jvm:$kotlinSerializationVersion") {
        exclude(group = "org.jetbrains", module = "annotations")
    }

    "shade"("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:$kotlinSerializationVersion") {
        exclude(group = "org.jetbrains", module = "annotations")
    }
}

// This block of code expands all declared replace properties in the specified resource targets.
// A missing property will result in an error. Properties are expanded using ${} Groovy notation.
// When "copyIdeResources" is enabled, this will also run before the game launches in IDE environments.
// See https://docs.gradle.org/current/dsl/org.gradle.language.jvm.tasks.ProcessResources.html
val resourceTargets = listOf("META-INF/mods.toml", "pack.mcmeta")
val replaceProperties = mapOf(
    "minecraft_version" to minecraftVersion, "minecraft_version_range" to minecraftVersionRange,
    "forge_version" to forgeVersion, "forge_version_range" to forgeVersionRange,
    "mod_id" to modId, "mod_name" to modName, "mod_license" to modLicense, "mod_version" to modVersion,
    "mod_authors" to modAuthors, "mod_description" to modDescription,
)
val Project.minecraft: UserDevExtension
    get() = extensions.getByName<UserDevExtension>("minecraft")

tasks.named<ProcessResources>("processResources") {
    inputs.properties(replaceProperties)
    val expansionMap = replaceProperties.toMutableMap()
    expansionMap["project"] = project.name

    filesMatching(resourceTargets) {
        expand(expansionMap)
    }
}
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
tasks.withType<JavaCompile>().configureEach {
    this.options.encoding = "UTF-8"
}
tasks.named<Jar>("jar") {
    archiveClassifier = "slim"
    manifest {
        attributes(
            "Specification-Title" to modId,
            "Specification-Vendor" to modAuthors,
            "Specification-Version" to "1",
            "Implementation-Title" to project.name,
            "Implementation-Version" to archiveVersion.get(),
            "Implementation-Vendor" to modAuthors,
            "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
        )
    }
//    finalizedBy("reobfJar")
}
// Ugly workaround :(
// tasks.matching { it.name.startsWith("reobf") }.configureEach {
//    mustRunAfter(tasks.shadowJar)
// }
tasks.shadowJar {
//    enableAutoRelocation = true
    archiveClassifier.set("")
    configurations = listOf(project.configurations.getByName("shade"))
    relocate("kotlinx.serialization", "club.pisquasd.csgrenades.shadow.serialization")
    from(sourceSets.main.get().output)
//    dependencies {
//        include("thedarkcolour:kotlinforforge:$kffVersion")
//    }
//    minimizeJar = true
    isZip64 = true
    manifest {
        attributes(
            "Specification-Title" to modId,
            "Specification-Vendor" to modAuthors,
            "Specification-Version" to "1",
            "Implementation-Title" to project.name,
            "Implementation-Version" to archiveVersion.get(),
            "Implementation-Vendor" to modAuthors,
            "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
        )
    }
    finalizedBy("reobfShadowJar")
}
// tasks.named("reobfJar") {
//    dependsOn(tasks.shadowJar)
// }
// tasks.named("reobfJarJar") {
//    dependsOn(tasks.shadowJar)
// }
tasks.assemble {
    dependsOn(tasks.shadowJar)
}
reobf {
    create("shadowJar")
}
// The below is only required if using "maven-publish" and you want to publish to maven or use JitPack
// publishing {
//    publications {
//        mavenJava(MavenPublication) {
//            artifact(jar)
//        }
//    }
//    repositories {
//        maven {
//            url("file://${project.projectDir}/mcmodsrepo")
//        }
//    }
// }
