import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.4.10"
    id("io.papermc.paperweight.userdev") version "2.0.0-SNAPSHOT"
}

val pluginVersion = providers.gradleProperty("pluginVersion").get()
group = "cc.modlabs.basicx"
version = pluginVersion
val expandedPluginVersion = version.toString()
val expandedProjectName = name

val minecraftVersion = providers.gradleProperty("minecraftVersion").get()
val kotlinxCoroutinesCoreVersion = providers.gradleProperty("kotlinxCoroutinesCoreVersion").get()
val kotlinxCollectionsImmutableVersion = providers.gradleProperty("kotlinxCollectionsImmutableVersion").get()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "PaperMC"
    }
    maven("https://repo-api.modlabs.cc/repo/maven/maven-public/") {
        name = "ModLabs"
    }
    maven("https://repo.helpch.at/releases/") {
        name = "PlaceholderAPI"
    }
    maven("https://repo.codemc.io/repository/maven-releases/") {
        name = "CodeMC"
    }
}

val runtimeLibraries = listOf(
    "org.jetbrains.kotlin:kotlin-stdlib:2.4.10",
    "org.jetbrains.kotlin:kotlin-reflect:2.4.10",
    "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesCoreVersion",
    "org.jetbrains.kotlinx:kotlinx-collections-immutable:$kotlinxCollectionsImmutableVersion",
    "cc.modlabs:KPaper:2026.7.13.0901",
)

dependencies {
    paperweight.paperDevBundle("$minecraftVersion.build.+")

    compileOnly("me.clip:placeholderapi:2.12.3")
    compileOnly("net.luckperms:api:5.5")

    runtimeLibraries.forEach { dependency ->
        implementation(dependency)
    }

    testImplementation(platform("org.junit:junit-bom:6.1.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.register("generateDependenciesFile") {
    group = "build"
    description = "Writes dependencies to file"

    val dependenciesFile = File(layout.buildDirectory.asFile.get(), "generated-resources/.dependencies")
    outputs.file(dependenciesFile)
    doLast {
        dependenciesFile.parentFile.mkdirs()
        dependenciesFile.writeText(runtimeLibraries.sorted().joinToString("\n", postfix = "\n"))
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    withType<ProcessResources> {
        dependsOn("generateDependenciesFile")

        from(File(layout.buildDirectory.asFile.get(), "generated-resources")) {
            include(".dependencies")
        }

        filesMatching("paper-plugin.yml") {
            expand(
                "version" to expandedPluginVersion,
                "name" to expandedProjectName,
            )
        }
    }
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/kotlin")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
        allWarningsAsErrors.set(true)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

paperweight {
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}

paperweight.reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION