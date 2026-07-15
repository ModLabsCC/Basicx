package cc.modlabs.basicx.managers

import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Logger

class FileConfig(private val fileName: String) : YamlConfiguration() {

    private val file = dataDirectory.resolve(fileName)
    private val wasCreated: Boolean

    init {
        require(fileName.isNotBlank() && !fileName.contains("..")) {
            "Invalid configuration file name: $fileName"
        }

        try {
            Files.createDirectories(file.parent)
            wasCreated = Files.notExists(file)
            if (wasCreated) {
                Files.createFile(file)
                logger.info("Created config file $fileName")
            }
            load(file.toFile())
        } catch (exception: IOException) {
            throw IllegalStateException("Failed to open configuration $file", exception)
        } catch (exception: InvalidConfigurationException) {
            throw IllegalStateException("Invalid YAML in configuration $file", exception)
        }
    }

    fun saveConfig() {
        try {
            save(file.toFile())
        } catch (exception: IOException) {
            throw IllegalStateException("Failed to save configuration $file", exception)
        }
    }

    fun saveDefaultConfig(): FileConfig {
        if (!wasCreated) return this

        val resource = javaClass.getResourceAsStream("/$fileName")
            ?: throw IllegalStateException("Bundled resource $fileName was not found")
        resource.use { input ->
            Files.newOutputStream(file).use(input::copyTo)
        }
        try {
            load(file.toFile())
        } catch (exception: IOException) {
            throw IllegalStateException("Failed to load default configuration $file", exception)
        } catch (exception: InvalidConfigurationException) {
            throw IllegalStateException("Bundled configuration $fileName contains invalid YAML", exception)
        }
        logger.info("Saved default config to $file")
        return this
    }

    companion object {
        private val logger = Logger.getLogger(FileConfig::class.java.name)

        @Volatile
        private var dataDirectory: Path = Path.of("plugins", "BasicX")

        fun configure(directory: Path) {
            dataDirectory = directory.toAbsolutePath().normalize()
        }

        internal fun resolve(fileName: String): Path = dataDirectory.resolve(fileName)
    }
}