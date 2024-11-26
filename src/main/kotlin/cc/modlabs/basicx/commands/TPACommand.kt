package cc.modlabs.basicx.commands

import cc.modlabs.basicx.cache.MessageCache
import cc.modlabs.basicx.extensions.sendMessagePrefixed
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun createTPACommand(): LiteralCommandNode<CommandSourceStack> {
    return Commands.literal("tpa")
        .then(Commands.argument("player", StringArgumentType.string())
            .executes { ctx -> executeTPA(ctx) })
        .build()
}

private val tpaRequests = mutableMapOf<Player, Player>()

private fun executeTPA(ctx: CommandContext<CommandSourceStack>): Int {
    val sender = ctx.source.sender
    if (sender !is Player) {
        sender.sendMessagePrefixed("commands.tpa.not-player", default = "Only players can use this command.")
        return Command.SINGLE_SUCCESS
    }

    val targetName = StringArgumentType.getString(ctx, "player")
    val target = Bukkit.getPlayer(targetName)
    if (target == null) {
        sender.sendMessagePrefixed("commands.tpa.player-not-found", default = "Player not found.")
        return Command.SINGLE_SUCCESS
    }

    if (target == sender) {
        sender.sendMessagePrefixed("commands.tpa.self-request", default = "You cannot send a teleport request to yourself.")
        return Command.SINGLE_SUCCESS
    }

    tpaRequests[target] = sender
    sender.sendMessagePrefixed("commands.tpa.request-sent", mapOf("target" to target.name), default = "Teleport request sent to {target}.")
    target.sendMessagePrefixed("commands.tpa.request-received", mapOf("sender" to sender.name), default = "{sender} has requested to teleport to you. Type /tpaccept to accept.")

    return Command.SINGLE_SUCCESS
}

fun acceptRequest(target: Player) {
    val sender = tpaRequests.remove(target)
    if (sender != null) {
        sender.teleport(target.location)
        sender.sendMessagePrefixed("commands.tpa.request-accepted", mapOf("target" to target.name), default = "Teleport request accepted. Teleporting to {target}.")
        target.sendMessagePrefixed("commands.tpa.request-accepted-target", mapOf("sender" to sender.name), default = "You have accepted {sender}'s teleport request.")
    } else {
        target.sendMessagePrefixed("commands.tpa.no-request", default = "You have no pending teleport requests.")
    }
}
