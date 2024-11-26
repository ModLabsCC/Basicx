package cc.modlabs.basicx

import cc.modlabs.basicx.cache.MessageCache
import cc.modlabs.basicx.managers.RegisterManager
import org.bukkit.plugin.java.JavaPlugin
import kotlin.system.measureTimeMillis

import cc.modlabs.basicx.commands.TPCommand
import cc.modlabs.basicx.commands.TPACommand
import cc.modlabs.basicx.commands.InvseeCommand
import cc.modlabs.basicx.commands.VanishCommand
import cc.modlabs.basicx.commands.GMCommand
import cc.modlabs.basicx.commands.ItemEditCommand
import cc.modlabs.basicx.commands.TrashCommand
import cc.modlabs.basicx.commands.FeedCommand
import cc.modlabs.basicx.commands.HealCommand
import cc.modlabs.basicx.commands.FlyCommand
import cc.modlabs.basicx.commands.AnvilCommand
import cc.modlabs.basicx.commands.WarpCommand
import cc.modlabs.basicx.commands.CreateWarpCommand
import cc.modlabs.basicx.commands.DeleteWarpCommand
import cc.modlabs.basicx.commands.HomesCommand
import cc.modlabs.basicx.commands.EconomyCommand
import cc.modlabs.basicx.commands.KitCommand
import cc.modlabs.basicx.commands.TimeCommand
import cc.modlabs.basicx.commands.WeatherCommand

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

        // Register commands
        getCommand("tp")?.setExecutor(TPCommand())
        getCommand("tpa")?.setExecutor(TPACommand())
        getCommand("invsee")?.setExecutor(InvseeCommand())
        getCommand("vanish")?.setExecutor(VanishCommand())
        getCommand("gm")?.setExecutor(GMCommand())
        getCommand("itemedit")?.setExecutor(ItemEditCommand())
        getCommand("trash")?.setExecutor(TrashCommand())
        getCommand("feed")?.setExecutor(FeedCommand())
        getCommand("heal")?.setExecutor(HealCommand())
        getCommand("fly")?.setExecutor(FlyCommand())
        getCommand("anvil")?.setExecutor(AnvilCommand())
        getCommand("warp")?.setExecutor(WarpCommand())
        getCommand("createwarp")?.setExecutor(CreateWarpCommand())
        getCommand("deletewarp")?.setExecutor(DeleteWarpCommand())
        getCommand("homes")?.setExecutor(HomesCommand())
        getCommand("economy")?.setExecutor(EconomyCommand())
        getCommand("kit")?.setExecutor(KitCommand())
        getCommand("time")?.setExecutor(TimeCommand())
        getCommand("weather")?.setExecutor(WeatherCommand())

        // Copy the messages file to the plugins folder
        saveResource("messages.yml", false)

        // Plugin startup logic
        val time = measureTimeMillis {
            MessageCache.loadCache()
        }

        RegisterManager.registerListeners(this)

        logger.info("Plugin enabled in $time ms")
        logger.info("BasicX is now tweaking your server behavior!")
    }
}
