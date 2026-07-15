package cc.modlabs.basicx.managers

import cc.modlabs.basicx.extensions.send
import cc.modlabs.basicx.modules.BasicXModule
import cc.modlabs.basicx.modules.ModuleStatus
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import java.util.logging.Logger
import kotlin.math.ceil
import kotlin.math.max

object ModuleManager {

    private val logger = Logger.getLogger(ModuleManager::class.java.name)
    private val allModules = mutableMapOf<BasicXModule, ModuleStatus>()
    val maxPages: Int
        get() = max(1, ceil(allModules.size / 8.0).toInt())

    init {
        loadEnabledModulesFromConfig()
    }

    /**
     * Prints a list of all modules to the console. (8 modules per page)
     *
     * @param sender The command sender.
     * @param page The page number to print.
     */
    fun printAllModules(sender: CommandSender = Bukkit.getConsoleSender(), page: Int = 1) {
        val previousPage = if (page == 1) 1 else page - 1
        val nextPage = if (page == maxPages) maxPages else page + 1
        sender.send("modules.list.header", mapOf(
            "previousPage" to previousPage,
            "page" to page,
            "maxpages" to maxPages,
            "nextPage" to nextPage
        ), default = "---------<color:#b2c2d4> <color:#f5f6fa><click:run_command:'/basicx module list {previousPage}'><hover:show_text:'Previous Page'>«</hover></click></color> Modules Page {page} / {maxpages} <color:#f5f6fa><click:run_command:'/basicx module list {nextPage}'><hover:show_text:'Next Page'>»</hover></click></color> </color>---------")
        allModules.keys.drop((page - 1) * 8).take(8).forEach { module ->
            when (allModules[module]) {
                ModuleStatus.ENABLED -> sendModuleStatusEnabled(sender, module)
                ModuleStatus.DISABLED -> sendModuleStatusDisabled(sender, module)
                ModuleStatus.RUNTIME_ENABLED -> sendModuleStatusRuntimeEnabled(sender, module)
                ModuleStatus.RUNTIME_DISABLED -> sendModuleStatusRuntimeDisabled(sender, module)
                null -> return@forEach
            }
        }
        sender.send("modules.list.footer", mapOf(
            "previousPage" to previousPage,
            "page" to page,
            "maxpages" to maxPages,
            "nextPage" to nextPage
        ), default = "---------<color:#b2c2d4> <color:#f5f6fa><click:run_command:'/basicx module list {previousPage}'><hover:show_text:'Previous Page'>«</hover></click></color> Modules Page {page} / {maxpages} <color:#f5f6fa><click:run_command:'/basicx module list {nextPage}'><hover:show_text:'Next Page'>»</hover></click></color> </color>---------")
    }

    private fun sendModuleStatusEnabled(sender: CommandSender, module: BasicXModule) {
        sender.send("modules.list.enabled", mapOf("module" to module.packageName), default = "<color:#6ab04c><hover:show_text:'<#badc58>Click to disable'><click:run_command:'/basicx module disable {module}'>⚡ <#dfe6e9>{module}</click></hover></color>")
    }

    private fun sendModuleStatusDisabled(sender: CommandSender, module: BasicXModule) {
        sender.send("modules.list.disabled", mapOf("module" to module.packageName), default = "<color:#eb4d4b><hover:show_text:'<#badc58>Click to enable'><click:run_command:'/basicx module enable {module}'>⚡ <#dfe6e9>{module}</click></hover></color>")
    }

    private fun sendModuleStatusRuntimeEnabled(sender: CommandSender, module: BasicXModule) {
        sender.send("modules.list.runtime-enabled", mapOf("module" to module.packageName), default = "<color:#badc58><hover:show_text:'<#badc58>This module was enabled at runtime. Some features might work not as expected. Click to disable'><click:run_command:'/basicx module disable {module}'>⚡ <#dfe6e9>{module}</click></hover></color>")
    }

    private fun sendModuleStatusRuntimeDisabled(sender: CommandSender, module: BasicXModule) {
        sender.send("modules.list.runtime-disabled", mapOf("module" to module.packageName), default = "<color:#ffbe76><hover:show_text:'<#badc58>This module will be disabled on restart. Click to re-enable'><click:run_command:'/basicx module enable {module}'>⚡ <#dfe6e9>{module}</click></hover></color>")
    }


    fun loadEnabledModulesFromConfig() {
        val config = FileConfig("config.yml").saveDefaultConfig()
        allModules.clear()
        var addedDefaults = false
        BasicXModule.entries.forEach { module ->
            val path = "modules.${module.packageName}.enabled"
            if (!config.contains(path)) {
                config[path] = true
                addedDefaults = true
            }
            allModules[module] = if (config.getBoolean(path)) {
                ModuleStatus.ENABLED
            } else {
                ModuleStatus.DISABLED
            }
        }
        if (addedDefaults) config.saveConfig()
        logger.info("Loaded ${allModules.size} modules - Enabled: ${allModules.filterValues { it == ModuleStatus.ENABLED }.size}")
    }

    fun startUp(plugin: Plugin) {
        BasicXModule.entries.forEach { module ->
            RegisterManager.registerListenersForModule(plugin, module.packageName)
        }
    }

    /**
     * Enables a module at runtime.
     *
     * @param basicXModule The module to enable.
     * @param plugin The plugin instance.
     */
    fun enableModule(basicXModule: BasicXModule) {
        allModules[basicXModule] = ModuleStatus.RUNTIME_ENABLED
        setModuleEnabled(basicXModule, true)
    }

    fun isModuleEnabled(basicXModule: BasicXModule): Boolean {
        return allModules.contains(basicXModule) && (allModules[basicXModule] == ModuleStatus.RUNTIME_ENABLED || allModules[basicXModule] == ModuleStatus.ENABLED)
    }

    fun getModuleStatus(basicXModule: BasicXModule): ModuleStatus {
        return allModules[basicXModule] ?: ModuleStatus.DISABLED
    }

    fun disableModule(basicXModule: BasicXModule) {
        allModules[basicXModule] = ModuleStatus.RUNTIME_DISABLED
        setModuleEnabled(basicXModule, false)
    }

    private fun setModuleEnabled(basicXModule: BasicXModule, enabled: Boolean) {
        val config = FileConfig("config.yml")
        config["modules.${basicXModule.packageName}.enabled"] = enabled
        config.saveConfig()
    }

}