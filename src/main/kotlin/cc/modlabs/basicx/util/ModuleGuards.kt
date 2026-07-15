package cc.modlabs.basicx.util

import cc.modlabs.basicx.managers.ModuleManager
import cc.modlabs.basicx.modules.BasicXModule
import io.papermc.paper.command.brigadier.CommandSourceStack

fun CommandSourceStack.canUseModule(module: BasicXModule, permission: String): Boolean =
    ModuleManager.isModuleEnabled(module) && sender.hasPermission(permission)
