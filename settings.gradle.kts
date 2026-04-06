pluginManagement {
    repositories {
        maven("https://maven.deftu.dev/releases")
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev/")
        maven("https://maven.neoforged.net/releases")
        maven("https://repo.essential.gg/repository/maven-public")
        maven("https://maven.jab125.dev/")
        maven("https://maven.wagyourtail.xyz/releases")
        maven("https://maven.jemnetworks.com/releases")
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "world-host"
rootProject.buildFileName = "build.gradle.kts"

listOf(
    "1.21.11-fabric",
    "1.21.11-neoforge",
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        projectDir.mkdirs()
        buildFileName = "../../version.gradle.kts"
    }
}
