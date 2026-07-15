package cc.modlabs.basicx

import cc.modlabs.basicx.cache.*
import cc.modlabs.basicx.integrations.LuckPermsIntegration
import cc.modlabs.basicx.managers.AnvilSessionManager
import cc.modlabs.basicx.managers.ConfigWriter
import cc.modlabs.basicx.managers.FileConfig
import cc.modlabs.basicx.managers.ModuleManager
import cc.modlabs.basicx.managers.TeleportRequestManager
import cc.modlabs.basicx.managers.VanishManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import kotlin.system.measureTimeMillis

class BasicX : JavaPlugin() {

    companion object {
        lateinit var instance: BasicX
            private set
    }

    override fun onEnable() {
        instance = this
        FileConfig.configure(dataFolder.toPath())
        logger.info("Enabling BasicX...")

        if (!dataFolder.resolve("messages.yml").exists()) {
            saveResource("messages.yml", false)
        }
        if (!dataFolder.resolve("kits.yml").exists()) {
            saveResource("kits.yml", false)
        }

        // Plugin startup logic
        val time = measureTimeMillis {
            MessageCache.loadCache()
            WarpCache.loadCache()
            HomeCache.loadCache()
            KitCache.loadCache()
            Bukkit.getOnlinePlayers().forEach { OnlinePlayerCache.add(it.name) }

            ModuleManager.startUp(this)
        }

        if (server.pluginManager.isPluginEnabled("LuckPerms")) {
            try {
                LuckPermsIntegration.load()
            } catch (exception: Exception) {
                logger.warning("Failed to load LuckPerms API: ${exception.message}")
            } catch (error: LinkageError) {
                logger.warning("LuckPerms integration is unavailable: ${error.message}")
            }
        }

        logger.info("Plugin enabled in $time ms")
        logger.info("BasicX is now tweaking your server behavior!")
    }

    override fun onDisable() {
        HomeCache.flush()
        WarpCache.flush()
        KitCache.flush()
        ConfigWriter.shutdown()
        TablistCache.clear()
        OnlinePlayerCache.clear()
        TeleportRequestManager.clear()
        VanishManager.clear()
        AnvilSessionManager.clear()
    }
}
