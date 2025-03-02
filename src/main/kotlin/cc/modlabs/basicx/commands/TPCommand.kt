package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.send
import cc.modlabs.kpaper.extensions.sendTeleportSound
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

fun createTPCommand(): LiteralCommandNode<CommandSourceStack> {
    return Commands.literal("tp")
        .then(Commands.argument("player", StringArgumentType.string())
            .executes { ctx -> executeTPPlayer(ctx) })
        .then(Commands.argument("x", DoubleArgumentType.doubleArg())
            .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                    .executes { ctx -> executeTPCoordinates(ctx) })))
        .build()
}

private fun executeTPPlayer(ctx: CommandContext<CommandSourceStack>): Int {
    val sender = ctx.source.sender
    if (sender !is Player) {
        sender.sendMessage("This command can only be used by players.")
        return Command.SINGLE_SUCCESS
    }

    val targetName = StringArgumentType.getString(ctx, "player")
    val target = Bukkit.getPlayer(targetName)
    if (target == null) {
        sender.send("commands.tp.player-not-found", default = "Player not found.")
        return Command.SINGLE_SUCCESS
    }

    sender.teleport(target.location)
    sender.send("commands.tp.success", mapOf("player" to target.name), default = "Teleported to {player}.")
    sender.sendTeleportSound()
    return Command.SINGLE_SUCCESS
}

private fun executeTPCoordinates(ctx: CommandContext<CommandSourceStack>): Int {
    val sender = ctx.source.sender
    if (sender !is Player) {
        sender.sendMessage("This command can only be used by players.")
        return Command.SINGLE_SUCCESS
    }

    val x = DoubleArgumentType.getDouble(ctx, "x")
    val y = DoubleArgumentType.getDouble(ctx, "y")
    val z = DoubleArgumentType.getDouble(ctx, "z")

    val location = Location(sender.world, x, y, z)
    sender.teleport(location)
    sender.send("commands.tp.success-coordinates", mapOf("x" to x, "y" to y, "z" to z), default = "Teleported to {x}, {y}, {z}.")
    sender.sendTeleportSound()
    return Command.SINGLE_SUCCESS
}
