package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.sendMessagePrefixed
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.GameMode
import org.bukkit.entity.Player

fun createGMCommand(): LiteralCommandNode<CommandSourceStack> {
    return Commands.literal("gm")
        .requires { it.hasPermission("basicx.gm") }
        .then(Commands.argument("mode", IntegerArgumentType.integer(0, 3))
            .executes { ctx -> setGameMode(ctx) }
        )
        .build()
}

private fun setGameMode(ctx: CommandContext<CommandSourceStack>): Int {
    val sender = ctx.source.sender
    if (sender !is Player) {
        sender.sendMessagePrefixed("commands.gm.not-player", default = "Only players can use this command.")
        return Command.SINGLE_SUCCESS
    }

    val mode = IntegerArgumentType.getInteger(ctx, "mode")
    val gameMode = when (mode) {
        0 -> GameMode.SURVIVAL
        1 -> GameMode.CREATIVE
        2 -> GameMode.ADVENTURE
        3 -> GameMode.SPECTATOR
        else -> {
            sender.sendMessagePrefixed("commands.gm.invalid-mode", default = "Invalid game mode.")
            return Command.SINGLE_SUCCESS
        }
    }

    sender.gameMode = gameMode
    sender.sendMessagePrefixed("commands.gm.success", mapOf("mode" to gameMode.name), default = "Game mode set to {mode}.")
    return Command.SINGLE_SUCCESS
}
