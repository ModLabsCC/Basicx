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
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CreateWarpCommand {

    fun createWarpCommand(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("createwarp")
            .requires { it.sender.hasPermission("basicx.createwarp") }
            .then(Commands.argument("warpName", StringArgumentType.string())
                .executes { ctx ->
                    val sender = ctx.source.sender
                    val warpName = StringArgumentType.getString(ctx, "warpName")
                    createWarp(sender, warpName)
                    Command.SINGLE_SUCCESS
                }
            )
            .build()
    }

    private fun createWarp(sender: CommandSender, warpName: String) {
        val player = sender as? Player ?: return
        val location = player.location

        WarpCommand.addWarp(warpName, location)
        player.sendMessagePrefixed("commands.createwarp.success", mapOf("warpName" to warpName), default = "Warp {warpName} created")
    }
}
