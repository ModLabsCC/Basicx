# AGENTS.md

## Cursor Cloud specific instructions

BasicX is a single-product repo: a modular **Paper (Minecraft) server plugin** written in Kotlin and built with Gradle (Kotlin DSL) via the checked-in wrapper. There is no long-running dev server — the deliverable is a plugin JAR in `build/libs` that is loaded by a Paper server. Standard commands live in `README.md`, `CONTRIBUTING.md`, and `docs/SMOKE_TEST.md`; the notes below only capture non-obvious environment gotchas.

### Build / lint / test
- Build + unit tests (JUnit 5): `./gradlew build` (CI/full check: `./gradlew clean build --warning-mode all`).
- There is no separate lint task. `build.gradle.kts` sets `allWarningsAsErrors`, so any Kotlin compiler warning fails the build — treat compiler warnings as lint failures.

### Java toolchain (important gotcha)
- The Gradle wrapper itself runs fine on the VM's default **Java 21**; you do not need to change `JAVA_HOME` to build.
- Compilation targets **Java 25**, which Gradle auto-provisions via the foojay resolver on the first build (downloads a Temurin 25 JDK, ~200 MB). The provisioned JDK lands under `~/.gradle/jdks/eclipse_adoptium-25-amd64-linux.*/`.
- The first build also runs paperweight `paperweightUserdevSetup` (downloads + remaps the Paper dev bundle, ~1 min). Both are cached, so only the first build in a fresh environment is slow.
- Network access to Maven Central, `repo.papermc.io`, `repo-api.modlabs.cc`, `repo.codemc.io`, and `repo.helpch.at` is required at build time.

### Running the plugin end-to-end (manual integration test)
- To actually run the plugin you need a Paper **26.2** server, and Paper 26.2 requires **Java 25 to run** (not just to compile). Use the provisioned toolchain JVM, e.g. `~/.gradle/jdks/eclipse_adoptium-25-amd64-linux.*/bin/java`, not the default `java` (21).
- Recipe: download the latest Paper 26.2 jar from the PaperMC Fill API (`https://fill.papermc.io/v3/projects/paper/versions/26.2/builds/latest`), put it in a scratch dir, write `eula=true` to `eula.txt`, copy `build/libs/Basicx-*.jar` into `plugins/`, then start with the Java 25 launcher (`java -jar paper.jar --nogui`).
- On first enable the plugin downloads its unshaded runtime libraries (Kotlin, coroutines, KPaper) via Paper's isolated library loader and generates `plugins/BasicX/{config.yml,messages.yml,kits.yml,homes.yml,warps.yml}`.
- Console management commands (no player needed): `basicx`, `basicx reload`, `basicx module enable|disable <module>`. Player-facing commands (`/home`, `/tpa`, `/kit`, etc.) require a connected client.
- Keep any test server in a directory that is git-ignored (the repo ignores `/plugins/`, `/logs/`, `/world*/`, `/server/`, `/run/`, `/eula.txt`); do not commit server files or generated plugin data.
