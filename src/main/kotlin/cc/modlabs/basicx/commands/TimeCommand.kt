package cc.modlabs.basicx.commands

import cc.modlabs.basicx.BasicX
import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import dev.fruxz.ascend.extension.logging.getLogger
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit

fun createTimeCommand(): LiteralCommandNode<CommandSourceStack> {
    return Commands.literal("time")
        .requires { it.sender.hasPermission("basicx.time") }
        .then(Commands.literal("set")
            .then(Commands.argument("time", StringArgumentType.word())
                .suggests { context, builder ->
                    builder.suggest("day")
                    builder.suggest("night")
                    builder.suggest("noon")
                    builder.suggest("midnight")
                    val input = context.input.split(" ")
                    if (input.isEmpty()) return@suggests builder.buildFuture()
                    val time = input.last()
                    if (time.first().isDigit()) {
                        for (i in 0..24) {
                            builder.suggest("${i}h")
                        }
                    }
                    return@suggests builder.buildFuture()
                }
                .executes { ctx ->
                    val timeInput = StringArgumentType.getString(ctx, "time")
                    val time = parseTimeInput(timeInput)
                    if (time == null) {
                        ctx.source.sender.send("commands.time.invalid-time", default = "Invalid time format")
                        return@executes 0
                    }
                    Bukkit.getWorlds().forEach { it.time = time }
                    ctx.source.sender.send("commands.time.set", mapOf("time" to time), default = "Time set to {time}")
                    return@executes Command.SINGLE_SUCCESS
                }
            )
        )
        .then(Commands.literal("add")
            .then(Commands.argument("time", StringArgumentType.word())
                .suggests { context, builder ->
                    for (i in 0..24) {
                        builder.suggest("${i}h")
                    }
                    return@suggests builder.buildFuture()
                }
                .executes { ctx ->
                    val timeInput = StringArgumentType.getString(ctx, "time")
                    val timeToAdd = parseTimeInput(timeInput)
                    if (timeToAdd == null) {
                        ctx.source.sender.send("commands.time.invalid-time", default = "Invalid time format")
                        return@executes 0
                    }
                    Bukkit.getWorlds().forEach { it.time = (it.time + timeToAdd) % 24000L }
                    ctx.source.sender.send("commands.time.add", mapOf("time" to timeToAdd), default = "Added {time}t to the time")
                    return@executes Command.SINGLE_SUCCESS
                }
            )
        )
        .build()
}

fun parseTimeInput(input: String): Long? {
    return when (input.lowercase()) {
        "day" -> 1000L
        "noon" -> 6000L
        "night" -> 13000L
        "midnight" -> 18000L
        else -> {
            if (input.endsWith("h")) {
                val hours = input.dropLast(1).toLongOrNull()
                if (hours != null) {
                    (24000L + (hours * 1000L) - 6000L) % 24000L
                } else null
            } else {
                input.toLongOrNull()?.let { it % 24000L }
            }
        }
    }
}
