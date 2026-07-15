package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.send
import cc.modlabs.basicx.managers.TeleportRequestManager
import cc.modlabs.basicx.modules.BasicXModule
import cc.modlabs.basicx.util.canUseModule
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
        .requires { it.canUseModule(BasicXModule.TPA, "basicx.tpa") }
        .then(Commands.argument("player", StringArgumentType.string())
            .executes { ctx -> executeTPA(ctx) })
        .build()
}

fun createTPAcceptCommand(): LiteralCommandNode<CommandSourceStack> =
    Commands.literal("tpaccept")
        .requires { it.canUseModule(BasicXModule.TPA, "basicx.tpa") }
        .executes { ctx -> resolveRequest(ctx, accepted = true) }
        .build()

fun createTPDenyCommand(): LiteralCommandNode<CommandSourceStack> =
    Commands.literal("tpdeny")
        .requires { it.canUseModule(BasicXModule.TPA, "basicx.tpa") }
        .executes { ctx -> resolveRequest(ctx, accepted = false) }
        .build()

private fun executeTPA(ctx: CommandContext<CommandSourceStack>): Int {
    val sender = ctx.source.sender
    if (sender !is Player) {
        sender.send("commands.tpa.not-player", default = "Only players can use this command.")
        return Command.SINGLE_SUCCESS
    }

    val targetName = StringArgumentType.getString(ctx, "player")
    val target = Bukkit.getPlayer(targetName)
    if (target == null) {
        sender.send("commands.tpa.player-not-found", default = "Player not found.")
        return Command.SINGLE_SUCCESS
    }

    if (target == sender) {
        sender.send("commands.tpa.self-request", default = "You cannot send a teleport request to yourself.")
        return Command.SINGLE_SUCCESS
    }

    TeleportRequestManager.create(sender.uniqueId, target.uniqueId)
    sender.send("commands.tpa.request-sent", mapOf("target" to target.name), default = "Teleport request sent to {target}.")
    target.send(
        "commands.tpa.request-received",
        mapOf("sender" to sender.name),
        default = "{sender} requested to teleport to you. Use /tpaccept or /tpdeny within 60 seconds.",
    )

    return Command.SINGLE_SUCCESS
}

private fun resolveRequest(ctx: CommandContext<CommandSourceStack>, accepted: Boolean): Int {
    val target = ctx.source.sender as? Player ?: run {
        ctx.source.sender.send("commands.tpa.not-player", default = "Only players can use this command.")
        return Command.SINGLE_SUCCESS
    }
    val requesterId = if (accepted) {
        TeleportRequestManager.accept(target.uniqueId)
    } else {
        TeleportRequestManager.deny(target.uniqueId)
    }
    val requester = requesterId?.let(Bukkit::getPlayer)
    if (requester == null) {
        target.send("commands.tpa.no-request", default = "You have no pending teleport requests.")
        return Command.SINGLE_SUCCESS
    }

    if (accepted) {
        requester.teleportAsync(target.location)
        requester.send(
            "commands.tpa.request-accepted",
            mapOf("target" to target.name),
            default = "Teleport request accepted. Teleporting to {target}.",
        )
        target.send(
            "commands.tpa.request-accepted-target",
            mapOf("sender" to requester.name),
            default = "You accepted {sender}'s teleport request.",
        )
    } else {
        requester.send(
            "commands.tpa.request-denied",
            mapOf("target" to target.name),
            default = "{target} denied your teleport request.",
        )
        target.send(
            "commands.tpa.request-denied-target",
            mapOf("sender" to requester.name),
            default = "You denied {sender}'s teleport request.",
        )
    }

    return Command.SINGLE_SUCCESS
}
