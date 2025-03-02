package cc.modlabs.basicx.managers

import cc.modlabs.klassicx.extensions.getLogger
import dev.fruxz.ascend.extension.createFileAndDirectories
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.nio.file.FileSystems

class FileConfig(private val fileName: String, fromRoot: Boolean = false) : YamlConfiguration() {

    private var seperator: String = FileSystems.getDefault().separator ?: "/"
    private var wasCreated: Boolean = false
    private val path: String = if (fromRoot) {
        fileName
    } else {
        "plugins${seperator}BasicX$seperator$fileName"
    }

    fun saveConfig() {
        try {
            save(path)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    init {

        val file = File(path)
        try {
            if (!file.exists()) {
                File(path).createFileAndDirectories()
                wasCreated = true
                getLogger().info("Created config file $fileName")
            }
            load(path)
        } catch (_: IOException) {
            // Do nothing
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    fun saveDefaultConfig(): FileConfig {
        if (wasCreated) {
            getLogger().info("Config file $fileName did not exists, saving default config")
            try {
                val resource = this::class.java.getResourceAsStream("/$fileName")
                if (resource == null) {
                    getLogger().error("Failed to save default config, resource $fileName not found")
                    return this
                }
                resource.copyTo(File(path).outputStream())
                getLogger().info("Saved default config to $path")
                load(path)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return this
    }
}