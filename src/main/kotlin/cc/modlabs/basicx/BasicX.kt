package cc.modlabs.basicx

import cc.modlabs.basicx.cache.*
import cc.modlabs.basicx.managers.ModuleManager
import cc.modlabs.kpaper.main.KPlugin
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import kotlin.system.measureTimeMillis

class BasicX : KPlugin() {

    companion object {
        lateinit var instance: BasicX
            private set
    }

    init {
        instance = this
    }

    override fun startup() {
        logger.info("Enabling BasicX...")

        // Copy the messages file to the plugins folder
        saveResource("messages.yml", false)
        saveResource("kits.yml", false)

        // Plugin startup logic
        val time = measureTimeMillis {
            MessageCache.loadCache()
            WarpCache.loadCache()
            HomeCache.loadCache()
            KitCache.loadCache()

            ModuleManager.startUp(this)
        }

        try {
            val provider = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)
            if (provider != null) {
                val luckPermsAPI = provider.provider
                TablistCache.loadLuckPerms(luckPermsAPI)
            }
        } catch (e: Exception) {
            logger.warning("Failed to load LuckPerms API")
        }

        logger.info("Plugin enabled in $time ms")
        logger.info("BasicX is now tweaking your server behavior!")
    }
}
