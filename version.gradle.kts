import com.replaymod.gradle.preprocess.PreprocessTask
import java.net.NetworkInterface
import java.util.*

plugins {
    java
    `maven-publish`
    id("io.github.gaming32.gradle.preprocess")
    id("dev.architectury.loom")
    id("com.modrinth.minotaur")
}

group = "io.github.gaming32"

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

val modVersion = project.properties["mod.version"] as String
val mcVersionString by extra(name.substringBefore("-"))
val loaderName by extra(name.substringAfter("-"))

val isFabric = loaderName == "fabric"
val isNeoForge = loaderName == "neoforge"

base.archivesName.set(rootProject.name)

// 1.21.11 -> 1_21_11
val mcVersion by extra(1_21_11)

println("MC_VERSION: $mcVersionString $mcVersion")
version = "${modVersion}+${mcVersionString}-${loaderName}"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net")
    maven("https://maven.neoforged.net/releases")
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.compileJava {
    options.release = 21
    options.compilerArgs.add("-Xlint:all")
}

loom {
    @Suppress("UnstableApiUsage")
    mixin {
        useLegacyMixinAp = false
    }

    runs {
        getByName("client") {
            ideConfigGenerated(true)
            runDir = "run/client"
        }
        remove(getByName("server"))

        val usernameSuffix = Collections.list(NetworkInterface.getNetworkInterfaces())
            .firstNotNullOfOrNull(NetworkInterface::getHardwareAddress)
            ?.toHexString()
            ?.take(10)
            ?: "0000000000"
        for (name in listOf("Host", "Joiner")) {
            val runName = "test$name"
            val user = name.uppercase()
            register(runName) {
                inherit(getByName("client"))
                ideConfigGenerated(false)

                configName = "Test $user"
                runDir = "run/$runName"

                val username = "$user$usernameSuffix"
                programArgs(
                    "--username", username,
                    "--uuid", UUID.nameUUIDFromBytes("OfflinePlayer:$username".encodeToByteArray()).toString()
                )
                vmArgs(
                    "-Dworld-host-testing.enabled=true",
                    "-Dworld-host-testing.user=$user",
                    "-Ddevauth.enabled=false"
                )
            }
        }
    }
}

repositories {
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://maven.terraformersmc.com/releases")
    maven("https://maven.isxander.dev/releases")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven("https://repo.viaversion.com")
    maven("https://maven.maxhenkel.de/repository/public")
    maven("https://api.modrinth.com/maven") {
        content {
            includeGroup("maven.modrinth")
        }
    }
}

println("loaderName: $loaderName")
println("mcVersion: $mcVersion")

dependencies {
    minecraft("com.mojang:minecraft:$mcVersionString")
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings {
            nameSyntheticMembers = true
        }
    })

    when {
        isFabric -> modImplementation("net.fabricmc:fabric-loader:0.18.1")
        isNeoForge -> "neoForge"("net.neoforged:neoforge:21.11.41-beta")
    }

    fun simpleJavaLibrary(notation: Any) = minecraftRuntimeLibraries(include(implementation(notation)!!)!!)

    simpleJavaLibrary("org.quiltmc.parsers:json:0.3.0")
    simpleJavaLibrary("org.semver4j:semver4j:5.3.0")

    if (isFabric) {
        modImplementation("com.terraformersmc:modmenu:17.0.0")
    }

    when {
        isFabric -> "fabric"
        isNeoForge -> "neoforge"
        else -> null
    }?.let { modRuntimeOnly("me.djtheredstoner:DevAuth-$it:1.2.1") }

    if (isFabric) {
        val fapiVersion = "0.139.5+1.21.11"
        val resourceLoader = fabricApi.module("fabric-resource-loader-v0", fapiVersion)
        include(modImplementation(resourceLoader)!!)

        for (module in listOf(
            "fabric-screen-api-v1",
            "fabric-key-binding-api-v1",
            "fabric-lifecycle-events-v1"
        )) {
            modRuntimeOnly(fabricApi.module(module, fapiVersion))
        }
    }

    if (isFabric) {
        modCompileOnly("dev.isxander:main-menu-credits:1.1.2") {
            isTransitive = false
        }
    }

    if (isFabric) {
        modCompileOnly("maven.modrinth:viafabricplus:3.0.2") {
            isTransitive = false
        }
    }

    compileOnly("de.maxhenkel.voicechat:voicechat-api:2.6.13")
    modCompileOnly("maven.modrinth:simple-voice-chat:$loaderName-$mcVersionString-2.6.13")

    compileOnly("com.demonwav.mcdev:annotations:2.1.0")

    // Resolves javac warnings about Guava
    compileOnly("com.google.errorprone:error_prone_annotations:2.11.0")
}

preprocess {
    fun Boolean.toInt() = if (this) 1 else 0

    vars.putAll(mapOf(
        "FABRIC" to isFabric.toInt(),
        "NEOFORGE" to isNeoForge.toInt(),
        "MC" to mcVersion,
    ))

    patternAnnotation.set("io.github.gaming32.worldhost.versions.Pattern")
    keywords.value(keywords.get())
    keywords.put(".json", PreprocessTask.DEFAULT_KEYWORDS.copy(eval = "//??"))
    keywords.put(".toml", PreprocessTask.CFG_KEYWORDS.copy(eval = "#??"))
}

//println("Parallel: ${gradle.startParameter.isParallelProjectExecutionEnabled}")

modrinth {
    val isStaging = true
    token.set(project.properties["modrinth.token"] as String? ?: System.getenv("MODRINTH_TOKEN"))
    projectId.set(if (isStaging) "world-host-staging" else "world-host")
    versionNumber.set(version.toString())
    val loaderName = when {
        isFabric -> "Fabric"
        isNeoForge -> "NeoForge"
        else -> throw IllegalStateException()
    }
    versionName.set("[$loaderName $mcVersionString] World Host $modVersion")
    uploadFile.set(tasks.named("remapJar"))
    additionalFiles.add(tasks.named("sourcesJar"))
    gameVersions.add(mcVersionString)
    gameVersions.add("1.21.10")
    loaders.add(this@Version_gradle.loaderName)
    dependencies {
        if (isFabric) {
            optional.project("modmenu")
        }
    }
    rootProject.file("changelogs/$modVersion.md").let {
        if (it.exists()) {
            println("Setting changelog file to $it")
            changelog.set(it.readText())
        } else {
            println("Changelog file $it does not exist!")
        }
    }
}

tasks.processResources {
    filesMatching("pack.mcmeta") {
        expand("pack_format" to 75)
    }

    filesMatching("fabric.mod.json") {
        filter {
            if (it.trim().startsWith("//")) "" else it
        }
    }

    filesMatching(listOf(
        "fabric.mod.json",
        "META-INF/neoforge.mods.toml",
        "*.mixins.json"
    )) {
        expand(mapOf(
            "version" to version,
            "mc_version" to mcVersionString
        ))
    }

    if (isFabric) {
        exclude("pack.mcmeta", "META-INF/neoforge.mods.toml")
    } else {
        exclude("fabric.mod.json")
        exclude("META-INF/mods.toml")
    }

    doLast {
        val resources = "${layout.buildDirectory.get()}/resources/main"
        if (isNeoForge) {
            copy {
                from(file("$resources/assets/world-host/icon.png"))
                into(resources)
            }
            delete(file("$resources/assets/world-host/icon.png"))
        } else {
            delete(file("$resources/pack.mcmeta"))
        }
    }
}

tasks.jar {
    archiveClassifier = "dev"
    from("$rootDir/LICENSE")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "world-host"
            artifact(tasks.named("remapJar"))
            artifact(tasks.named("sourcesJar")) {
                classifier = "sources"
            }
        }
    }
}
