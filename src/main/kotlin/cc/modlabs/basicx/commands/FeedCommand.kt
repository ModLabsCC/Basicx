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

class FeedCommand : Command<CommandSourceStack> {

    override fun run(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        val targetName = StringArgumentType.getString(context, "target")
        val target = Bukkit.getPlayer(targetName)

        if (target == null) {
            sender.send("commands.feed.target-not-found", mapOf("target" to targetName), default = "Player {target} not found.")
            return Command.SINGLE_SUCCESS
        }

        target.foodLevel = 20
        target.saturation = 20.0f
        target.send("commands.feed.success", default = "You have been fed.")
        sender.send("commands.feed.sender-success", mapOf("target" to targetName), default = "You have fed {target}.")

        return Command.SINGLE_SUCCESS
    }

    companion object {
        fun createFeedCommand(): LiteralCommandNode<CommandSourceStack> {
            return Commands.literal("feed")
                .requires { it.sender.hasPermission("basicx.feed") }
                .executes { context ->
                    val sender = context.source.sender
                    if (sender !is Player) {
                        sender.send("commands.feed.not-player", default = "Only players can use this command.")
                        return@executes Command.SINGLE_SUCCESS
                    }

                    sender.foodLevel = 20
                    sender.saturation = 20.0f
                    sender.send("commands.feed.success", default = "You have been fed.")
                    Command.SINGLE_SUCCESS
                }
                .then(Commands.argument("target", StringArgumentType.string())
                    .executes(FeedCommand())
                )
                .build()
        }
    }
}
