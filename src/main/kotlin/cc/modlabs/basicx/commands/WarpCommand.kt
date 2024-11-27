package cc.modlabs.basicx.commands

import cc.modlabs.basicx.BasicX
import cc.modlabs.basicx.cache.MessageCache
import cc.modlabs.basicx.cache.WarpCache
import cc.modlabs.basicx.extensions.sendMessagePrefixed
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object WarpCommand {

    fun createWarpCommand(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("warp")
            .requires { it.sender.hasPermission("basicx.warp") }
            .then(Commands.argument("warpName", StringArgumentType.string())
                .executes { ctx ->
                    val sender = ctx.source.sender
                    val warpName = StringArgumentType.getString(ctx, "warpName")
                    warp(sender, warpName)
                    Command.SINGLE_SUCCESS
                }
            )
            .build()
    }

    private fun warp(sender: CommandSender, warpName: String) {
        val player = sender as? Player ?: return
        val location = WarpCache.getWarp(warpName)

        if (location != null) {
            player.teleport(location)
            player.sendMessagePrefixed("commands.warp.success", mapOf("warpName" to warpName), default = "Warped to {warpName}")
        } else {
            player.sendMessagePrefixed("commands.warp.not-found", mapOf("warpName" to warpName), default = "Warp {warpName} not found")
        }
    }

    fun addWarp(warpName: String, location: Location) {
        WarpCache.addWarp(warpName, location)
    }

    fun removeWarp(warpName: String) {
        WarpCache.removeWarp(warpName)
    }
}
