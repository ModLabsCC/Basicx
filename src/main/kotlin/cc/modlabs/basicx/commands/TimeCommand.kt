package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit

fun createTimeCommand(): LiteralCommandNode<CommandSourceStack> {
    return Commands.literal("time")
        .requires { it.sender.hasPermission("basicx.time") }
        .then(Commands.literal("set")
            .then(Commands.argument("time", IntegerArgumentType.integer(0, 24000))
                .executes { ctx ->
                    val time = IntegerArgumentType.getInteger(ctx, "time")
                    Bukkit.getWorlds().forEach { it.time = time.toLong() }
                    ctx.source.sender.send("commands.time.set", mapOf("time" to time), default = "Time set to {time}")
                    return@executes Command.SINGLE_SUCCESS
                }
            )
        )
        .then(Commands.literal("add")
            .then(Commands.argument("time", IntegerArgumentType.integer(0, 24000))
                .executes { ctx ->
                    val time = IntegerArgumentType.getInteger(ctx, "time")
                    Bukkit.getWorlds().forEach { it.time += time.toLong() }
                    ctx.source.sender.send("commands.time.add", mapOf("time" to time), default = "Time added by {time}")
                    return@executes Command.SINGLE_SUCCESS
                }
            )
        )
        .build()
}
