package cc.modlabs.basicx.commands

import cc.modlabs.basicx.BasicX
import cc.modlabs.basicx.cache.MessageCache
import cc.modlabs.basicx.cache.HomeCache
import cc.modlabs.basicx.extensions.sendMessagePrefixed
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.UUID

object HomesCommand {

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

    private fun setHome(sender: CommandSender, homeName: String) {
        val player = sender as? Player ?: return
        val location = player.location
        val playerUUID = player.uniqueId

        HomeCache.addHome(playerUUID, homeName, location)
        player.sendMessagePrefixed("commands.homes.set.success", mapOf("homeName" to homeName), default = "Home {homeName} set")
    }

    private fun deleteHome(sender: CommandSender, homeName: String) {
        val player = sender as? Player ?: return
        val playerUUID = player.uniqueId

        HomeCache.removeHome(playerUUID, homeName)
        player.sendMessagePrefixed("commands.homes.delete.success", mapOf("homeName" to homeName), default = "Home {homeName} deleted")
    }

    private fun teleportHome(sender: CommandSender, homeName: String) {
        val player = sender as? Player ?: return
        val playerUUID = player.uniqueId
        val location = HomeCache.getHome(playerUUID, homeName)

        if (location != null) {
            player.teleport(location)
            player.sendMessagePrefixed("commands.homes.teleport.success", mapOf("homeName" to homeName), default = "Teleported to home {homeName}")
        } else {
            player.sendMessagePrefixed("commands.homes.not-found", mapOf("homeName" to homeName), default = "Home {homeName} not found")
        }
    }
}
