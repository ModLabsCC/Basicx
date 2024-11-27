package cc.modlabs.basicx.commands

import cc.modlabs.basicx.cache.WarpCache
import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

object WarpCommand {

    fun createWarpCommand(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("warp")
            .requires { it.sender.hasPermission("basicx.warp") }
            .executes { ctx ->
                val sender = ctx.source.sender
                if (sender !is Player) {
                    sender.send("commands.warp.list-console", mapOf("warps" to WarpCache.warpAmount), default = "There are currently <yellow>{warps}</yellow> warps set.")
                    return@executes Command.SINGLE_SUCCESS
                }

                // list all warps
                val warps = WarpCache.getWarps()
                if (warps.isEmpty()) {
                    sender.send("commands.warp.list-empty", default = "There are no warps set.")
                    return@executes Command.SINGLE_SUCCESS
                }

                sender.send("commands.warp.list", mapOf("warps" to WarpCache.warpAmount), default = "There are <yellow>{warps}</yellow> warps:")
                for (warp in warps) {
                    sender.send("commands.warp.list-entry", mapOf("warp" to warp), default = "» <yellow><click:run_command:'/warp {warp}'><hover:show_text:'Click to warp to {warp}'>{warp}</hover></click></yellow>")
                }


                Command.SINGLE_SUCCESS
            }
            .then(Commands.argument("warpName", StringArgumentType.string())
                .executes { ctx ->
                    val sender = ctx.source.sender
                    val warpName = StringArgumentType.getString(ctx, "warpName")
                    warp(sender, warpName)
                    Command.SINGLE_SUCCESS
                }
                .suggests(::suggestWarps)
            )
            .build()
    }

    private fun suggestWarps(
        ctx: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions?>? {
        for (home in WarpCache.getWarps()) {
            builder.suggest(home)
        }
        return builder.buildFuture()
    }

    private fun warp(sender: CommandSender, warpName: String) {
        val player = sender as? Player ?: return
        val location = WarpCache.getWarp(warpName)

        if (location != null) {
            player.teleport(location)
            player.send("commands.warp.success", mapOf("warpName" to warpName), default = "Warped to {warpName}")
        } else {
            player.send("commands.warp.not-found", mapOf("warpName" to warpName), default = "Warp {warpName} not found")
        }
    }

    fun addWarp(warpName: String, location: Location) {
        WarpCache.addWarp(warpName, location)
    }

    fun removeWarp(warpName: String) {
        WarpCache.removeWarp(warpName)
    }
}
