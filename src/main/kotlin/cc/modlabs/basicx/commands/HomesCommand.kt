package cc.modlabs.basicx.commands

import cc.modlabs.basicx.cache.HomeCache
import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

object HomesCommand {

    fun createHomesCommand(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("homes")
            .requires { it.sender.hasPermission("basicx.homes") }
            .executes { ctx ->
                val sender = ctx.source.sender
                if (sender !is Player) {
                    sender.send("commands.homes.list-console", mapOf("homes" to HomeCache.homeAmount, "players" to HomeCache.homedPlayers), default = "There are currently <yellow>{homes}</yellow> homes set by a total of <yellow>{players}</yellow> players.")
                    return@executes Command.SINGLE_SUCCESS
                }
               // list all homes for the player
                val homes = HomeCache.getHomes(sender.uniqueId)
                if (homes.isEmpty()) {
                    sender.send("commands.homes.list-empty", default = "You have no homes set.")
                    return@executes Command.SINGLE_SUCCESS
                }

                sender.send("commands.homes.list", default = "Your homes are:")
                for (home in homes) {
                    sender.send("commands.homes.list-entry", mapOf("home" to home), default = "» <yellow><click:run_command:'/home {home}'><hover:show_text:'Click to teleport to {home}'>{home}</hover></click></yellow>")
                }

                Command.SINGLE_SUCCESS
            }
            .then(Commands.literal("set")
                .then(Commands.argument("homeName", StringArgumentType.string())
                    .executes { ctx ->
                        val sender = ctx.source.sender
                        if (sender !is Player) {
                            sender.send("commands.homes.not-player", default = "Only players can use this command.")
                            return@executes Command.SINGLE_SUCCESS
                        }
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
                        if (sender !is Player) {
                            sender.send("commands.homes.not-player", default = "Only players can use this command.")
                            return@executes Command.SINGLE_SUCCESS
                        }
                        val homeName = StringArgumentType.getString(ctx, "homeName")
                        deleteHome(sender, homeName)
                        Command.SINGLE_SUCCESS
                    }
                    .suggests(::suggestHomes)
                )
            )
            .then(Commands.argument("homeName", StringArgumentType.string())
                .executes { ctx ->
                    val sender = ctx.source.sender
                    if (sender !is Player) {
                        sender.send("commands.homes.not-player", default = "Only players can use this command.")
                        return@executes Command.SINGLE_SUCCESS
                    }
                    val homeName = StringArgumentType.getString(ctx, "homeName")
                    teleportHome(sender, homeName)
                    Command.SINGLE_SUCCESS
                }
                .suggests(::suggestHomes)
            )
            .build()
    }

    private fun suggestHomes(
        ctx: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions?>? {
        val sender = ctx.source.sender
        if (sender !is Player) return builder.buildFuture()
        val playerUUID = sender.uniqueId
        for (home in HomeCache.getHomes(playerUUID)) {
            builder.suggest(home)
        }
        return builder.buildFuture()
    }

    private fun setHome(sender: CommandSender, homeName: String) {
        val player = sender as? Player ?: return
        val location = player.location
        val playerUUID = player.uniqueId

        HomeCache.addHome(playerUUID, homeName, location)
        player.send("commands.homes.set.success", mapOf("homeName" to homeName), default = "Home {homeName} set")
    }

    private fun deleteHome(sender: CommandSender, homeName: String) {
        val player = sender as? Player ?: return
        val playerUUID = player.uniqueId

        HomeCache.removeHome(playerUUID, homeName)
        player.send("commands.homes.delete.success", mapOf("homeName" to homeName), default = "Home {homeName} deleted")
    }

    private fun teleportHome(sender: CommandSender, homeName: String) {
        val player = sender as? Player ?: return
        val playerUUID = player.uniqueId
        val location = HomeCache.getHome(playerUUID, homeName)

        if (location != null) {
            player.teleport(location)
            player.send("commands.homes.teleport.success", mapOf("homeName" to homeName), default = "Teleported to home {homeName}")
        } else {
            player.send("commands.homes.not-found", mapOf("homeName" to homeName), default = "Home {homeName} not found")
        }
    }
}
