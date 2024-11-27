package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player


fun registerInvSeeCommand(): LiteralCommandNode<CommandSourceStack?>? {
    return Commands.literal("invsee")
        .then(Commands.argument("player", StringArgumentType.string())
            .executes { ctx -> execute(ctx) }
        )
        .build()
}

private fun execute(ctx: CommandContext<CommandSourceStack>): Int {
    val sender = ctx.source.sender
    if (sender !is Player) {
        sender.send("commands.invsee.not-a-player", default = "Only players can use this command.")
        return Command.SINGLE_SUCCESS
    }

    val targetName = StringArgumentType.getString(ctx, "player")
    val target = Bukkit.getPlayer(targetName)
    if (target == null) {
        sender.send("commands.invsee.player-not-found", default = "Player not found.")
        return Command.SINGLE_SUCCESS
    }

    sender.openInventory(target.inventory)
    sender.send("commands.invsee.success", mapOf("player" to target.name), default = "You are now viewing {player}'s inventory.")
    return Command.SINGLE_SUCCESS
}
