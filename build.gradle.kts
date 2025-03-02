import java.util.Calendar
import java.util.TimeZone

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.papermc.paperweight.userdev") version "2.0.0-SNAPSHOT"
    kotlin("plugin.serialization") version "2.1.10"
}

val pluginVersion: String by project

val dailyVersion = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin")).run {
    "${get(Calendar.YEAR)}.${get(Calendar.MONTH) + 1}.${get(Calendar.DAY_OF_MONTH)}"
}

group = "cc.modlabs.worldengine"
version = "$pluginVersion-$dailyVersion"

val minecraftVersion: String by project

val kotlinxCoroutinesCoreVersion: String by project
val kotlinxCollectionsImmutableVersion: String by project

repositories {
    maven("https://nexus.flawcra.cc/repository/maven-mirrors/")
}

val deliverDependencies = listOf(
    "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesCoreVersion",
    "org.jetbrains.kotlinx:kotlinx-collections-immutable:$kotlinxCollectionsImmutableVersion",

    "cc.modlabs:KPaper:2025.3.2.1238"
)

val includedDependencies = mutableListOf<String>()

fun Dependency?.deliver() = this?.apply {
    val computedVersion = version ?: kotlin.coreLibrariesVersion
    includedDependencies.add("${group}:${name}:${computedVersion}")
}

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.luckperms", "api", "5.4")

    implementation(kotlin("stdlib")).deliver()
    implementation(kotlin("reflect")).deliver()

    deliverDependencies.forEach { dependency ->
        implementation(dependency).deliver()
    }
}

tasks.register("generateDependenciesFile") {
    group = "build"
    description = "Writes dependencies to file"

    val dependenciesFile = File(layout.buildDirectory.asFile.get(), "generated-resources/.dependencies")
    outputs.file(dependenciesFile)
    doLast {
        dependenciesFile.parentFile.mkdirs()
        dependenciesFile.writeText(includedDependencies.joinToString("\n"))
    }
}


tasks {
    build {
        dependsOn(reobfJar)
    }

    withType<ProcessResources> {
        dependsOn("generateDependenciesFile")

        from(File(layout.buildDirectory.asFile.get(), "generated-resources")) {
            include(".dependencies")
        }

        filesMatching("paper-plugin.yml") {
            expand(
                "version" to project.version,
                "name" to project.name,
            )
        }
    }

    register<JavaCompile>("compileMain") {
        source = fileTree("src/main/java")
        classpath = files(configurations.runtimeClasspath)
        destinationDirectory.set(file("build/classes/kotlin/main"))
        options.release.set(21)
    }
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/kotlin")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=kotlin.RequiresOptIn"
            )
        )
    }
}