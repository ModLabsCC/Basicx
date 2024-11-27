package cc.modlabs.basicx.commands


import cc.modlabs.basicx.BasicX
import cc.modlabs.basicx.cache.MessageCache
import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
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
                sender.send("commands.basicx.info.reload-success", default = "<green>Config reloaded!")

                return@executes Command.SINGLE_SUCCESS
            }
        )
        .build()
}