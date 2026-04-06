plugins {
    id("io.github.gaming32.gradle.preprocess-root") version "0.4.5"
    id("dev.architectury.loom") version "1.15.5" apply false
    id("com.modrinth.minotaur") version "2.8.7" apply false
}

repositories {
    mavenCentral()
}

preprocess {
    val fabric12111 = createNode("1.21.11-fabric", 1_21_11)
    val neoforge12111 = createNode("1.21.11-neoforge", 1_21_11)

    fabric12111.link(neoforge12111)
}

subprojects {
    extra["loom.platform"] = name.substringAfter('-')
}

afterEvaluate {
    var previousPublishTask: Task? = null
    for (project in subprojects) {
        val publishTask = project.tasks.findByName("modrinth") ?: continue
        if (previousPublishTask != null) {
            publishTask.mustRunAfter(previousPublishTask)
        }
        previousPublishTask = publishTask
    }
}
