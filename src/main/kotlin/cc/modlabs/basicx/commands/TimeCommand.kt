package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.sendMessagePrefixed
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

fun createTimeCommand(): LiteralCommandNode<CommandSourceStack> {
    return Commands.literal("time")
        .requires { it.sender.hasPermission("basicx.time") }
        .then(Commands.literal("set")
            .then(Commands.argument("time", IntegerArgumentType.integer(0, 24000))
                .executes { ctx ->
                    val time = IntegerArgumentType.getInteger(ctx, "time")
                    Bukkit.getWorlds().forEach { it.time = time.toLong() }
                    ctx.source.sender.sendMessagePrefixed("commands.time.set", mapOf("time" to time), default = "Time set to $time")
                    return@executes Command.SINGLE_SUCCESS
                }
            )
        )
        .then(Commands.literal("add")
            .then(Commands.argument("time", IntegerArgumentType.integer(0, 24000))
                .executes { ctx ->
                    val time = IntegerArgumentType.getInteger(ctx, "time")
                    Bukkit.getWorlds().forEach { it.time += time.toLong() }
                    ctx.source.sender.sendMessagePrefixed("commands.time.add", mapOf("time" to time), default = "Time added by $time")
                    return@executes Command.SINGLE_SUCCESS
                }
            )
        )
        .build()
}
