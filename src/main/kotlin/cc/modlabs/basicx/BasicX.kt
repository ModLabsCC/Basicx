package cc.modlabs.basicx

import cc.modlabs.basicx.cache.MessageCache
import cc.modlabs.basicx.cache.WarpCache
import cc.modlabs.basicx.cache.HomeCache
import cc.modlabs.basicx.managers.RegisterManager
import org.bukkit.plugin.java.JavaPlugin
import kotlin.system.measureTimeMillis

class BasicX : JavaPlugin() {

    companion object {
        lateinit var instance: BasicX
            private set
    }

    init {
        instance = this
    }

    override fun onEnable() {
        logger.info("Enabling BasicX...")

        // Copy the messages file to the plugins folder
        saveResource("messages.yml", false)
        saveResource("warps.yml", false)
        saveResource("homes.yml", false)

        // Plugin startup logic
        val time = measureTimeMillis {
            MessageCache.loadCache()
            WarpCache.loadCache()
            HomeCache.loadCache()
        }

        RegisterManager.registerListeners(this)

        logger.info("Plugin enabled in $time ms")
        logger.info("BasicX is now tweaking your server behavior!")
    }
}
