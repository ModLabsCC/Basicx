package cc.modlabs.basicx.commands

import cc.modlabs.basicx.BasicX
import cc.modlabs.basicx.cache.MessageCache
import cc.modlabs.basicx.extensions.sendMessagePrefixed
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Location
import org.bukkit.entity.Player

object HomesCommand {

    private val homes = mutableMapOf<String, Location>()

    fun createHomesCommand(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("homes")
            .requires { it.sender.hasPermission("basicx.homes") }
            .then(Commands.literal("set")
                .then(Commands.argument("homeName", StringArgumentType.string())
                    .executes { ctx ->
                        val sender = ctx.source.sender
                        val homeName = StringArgumentType.getString(ctx, "homeName")
                        setHome(sender, homeName)
                        Command.SINGLE_SUCCESS
                    }
                )
            )
            .then(Commands.literal("delete")
                .then(Commands.argument("homeName", StringArgumentType.string())
                    .executes { ctx ->
                        val sender = ctx.source.sender
                        val homeName = StringArgumentType.getString(ctx, "homeName")
                        deleteHome(sender, homeName)
                        Command.SINGLE_SUCCESS
                    }
                )
            )
            .then(Commands.literal("teleport")
                .then(Commands.argument("homeName", StringArgumentType.string())
                    .executes { ctx ->
                        val sender = ctx.source.sender
                        val homeName = StringArgumentType.getString(ctx, "homeName")
                        teleportHome(sender, homeName)
                        Command.SINGLE_SUCCESS
                    }
                )
            )
            .build()
    }

    private fun setHome(sender: CommandSourceStack, homeName: String) {
        val player = sender.sender as? Player ?: return
        val location = player.location

        homes[homeName] = location
        player.sendMessagePrefixed("commands.homes.set.success", mapOf("homeName" to homeName), default = "Home {homeName} set")
    }

    private fun deleteHome(sender: CommandSourceStack, homeName: String) {
        val player = sender.sender as? Player ?: return

        homes.remove(homeName)
        player.sendMessagePrefixed("commands.homes.delete.success", mapOf("homeName" to homeName), default = "Home {homeName} deleted")
    }

    private fun teleportHome(sender: CommandSourceStack, homeName: String) {
        val player = sender.sender as? Player ?: return
        val location = homes[homeName]

        if (location != null) {
            player.teleport(location)
            player.sendMessagePrefixed("commands.homes.teleport.success", mapOf("homeName" to homeName), default = "Teleported to home {homeName}")
        } else {
            player.sendMessagePrefixed("commands.homes.not-found", mapOf("homeName" to homeName), default = "Home {homeName} not found")
        }
    }
}
