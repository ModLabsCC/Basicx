package cc.modlabs.basicx.commands


import cc.modlabs.basicx.BasicX
import cc.modlabs.basicx.cache.MessageCache
import cc.modlabs.basicx.extensions.send
import cc.modlabs.basicx.managers.ModuleManager
import cc.modlabs.basicx.modules.BasicXModule
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands

fun createBasicXCommand(): LiteralCommandNode<CommandSourceStack> {
    return Commands.literal("basicx")
        .executes { ctx ->
            val sender = ctx.source.sender
            sender.send("commands.basicx.info.version", mapOf("version" to BasicX.instance.description.version), default = "BasicX version {version}")
            return@executes Command.SINGLE_SUCCESS
        }
        .then(Commands.literal("reload")
            .requires { it.sender.hasPermission("basicx.manage") }
            .executes { ctx ->
                val sender = ctx.source.sender

                sender.send("commands.basicx.info.reload", default = "<yellow>Reloading config...")
                MessageCache.loadCache()
                sender.send("commands.basicx.info.reload-success", default = "<green>Messages reloaded!")
                sender.send("commands.basicx.info.reload-warn-config", default = "<#f0932b>Warning: For changes at the config.yml to take effect, you need to restart the server!")

                return@executes Command.SINGLE_SUCCESS
            }
        )
        .then(Commands.literal("module")
            .then(Commands.literal("list")
                .executes { ctx ->
                    val sender = ctx.source.sender
                    ModuleManager.printAllModules(sender)
                    return@executes Command.SINGLE_SUCCESS
                }
                .then(Commands.argument("page", IntegerArgumentType.integer(1, ModuleManager.maxPages))
                    .executes { ctx ->
                        val sender = ctx.source.sender
                        val page = IntegerArgumentType.getInteger(ctx, "page")
                        ModuleManager.printAllModules(sender, page)
                        return@executes Command.SINGLE_SUCCESS
                    }
                )
            )
            .then(Commands.literal("enable")
                .then(Commands.argument("module", StringArgumentType.string())
                    .executes { ctx ->
                        val sender = ctx.source.sender
                        val moduleName = StringArgumentType.getString(ctx, "module")
                        val module = BasicXModule.entries.firstOrNull { it.packageName == moduleName } ?: return@executes run {
                            sender.send("commands.basicx.module.enable.not-found", mapOf("module" to moduleName), default = "<red>Module {module} not found")
                            return@run Command.SINGLE_SUCCESS
                        }
                        ModuleManager.enableModule(module, BasicX.instance)

                        sender.send("commands.basicx.module.enable.success", mapOf("module" to module.packageName), default = "<green>Module {module} enabled")

                        return@executes Command.SINGLE_SUCCESS
                    }
                )
            )
            .then(Commands.literal("disable")
                .then(Commands.argument("module", StringArgumentType.string())
                    .executes { ctx ->
                        val sender = ctx.source.sender
                        val moduleName = StringArgumentType.getString(ctx, "module")
                        val module = BasicXModule.entries.firstOrNull { it.packageName == moduleName } ?: return@executes run {
                            sender.send("commands.basicx.module.enable.not-found", mapOf("module" to moduleName), default = "<red>Module {module} not found")
                            return@run Command.SINGLE_SUCCESS
                        }
                        ModuleManager.disableModule(module)

                        sender.send("commands.basicx.module.disable.success", mapOf("module" to module.packageName), default = "<green>Module {module} disabled")

                        return@executes Command.SINGLE_SUCCESS
                    }
                )
            )
        )
        .build()
}