package cc.modlabs.basicx.managers

import cc.modlabs.basicx.extensions.send
import cc.modlabs.basicx.modules.BasicXModule
import cc.modlabs.basicx.modules.ModuleStatus
import cc.modlabs.basicx.utils.FileConfig
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import kotlin.math.ceil

object ModuleManager {

    private val allModules = mutableMapOf<BasicXModule, ModuleStatus>()
    val maxPages: Int
        get() = ceil(allModules.size / 8.0).toInt()

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
        val config = FileConfig("config.yml")
        val modules = config.getConfigurationSection("modules")
        modules?.getKeys(false)?.forEach { module ->
            val status = if (config.getBoolean("modules.$module.enabled")) ModuleStatus.ENABLED else ModuleStatus.DISABLED
            val basicXModule = BasicXModule.entries.firstOrNull { it.packageName == module } ?: return@forEach

            if (allModules.contains(basicXModule)) error("Module $basicXModule is already loaded")
            allModules[basicXModule] = status
        }
    }

    fun startUp(plugin: Plugin) {
        allModules.forEach { (module, status) ->
            if (status == ModuleStatus.ENABLED) {
                RegisterManager.registerListenersForModule(plugin, module.packageName)
            }
        }
    }

    /**
     * Enables a module at runtime.
     *
     * @param basicXModule The module to enable.
     * @param plugin The plugin instance.
     */
    fun enableModule(basicXModule: BasicXModule, plugin: Plugin) {
        val previousStatus = allModules[basicXModule] ?: ModuleStatus.DISABLED
        allModules[basicXModule] = ModuleStatus.RUNTIME_ENABLED
        setModuleEnabled(basicXModule, true)

        if (previousStatus != ModuleStatus.RUNTIME_DISABLED) {
            RegisterManager.registerListenersForModule(plugin, basicXModule.packageName)
        }

        // Todo: Add command
    }

    fun isModuleEnabled(basicXModule: BasicXModule): Boolean {
        return allModules.contains(basicXModule) && (allModules[basicXModule] == ModuleStatus.RUNTIME_ENABLED || allModules[basicXModule] == ModuleStatus.ENABLED)
    }

    fun getModuleStatus(basicXModule: BasicXModule): ModuleStatus {
        return allModules[basicXModule] ?: ModuleStatus.DISABLED
    }

    fun disableModule(basicXModule: BasicXModule) {
        if (!allModules.contains(basicXModule)) error("Module $basicXModule is not enabled")
        allModules[basicXModule] = ModuleStatus.RUNTIME_DISABLED
        setModuleEnabled(basicXModule, false)
    }

    private fun setModuleEnabled(basicXModule: BasicXModule, enabled: Boolean) {
        val config = FileConfig("config.yml")
        config["modules.${basicXModule.packageName}.enabled"] = enabled
        config.saveConfig()
    }
}