package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object DeleteWarpCommand {

    fun createDeleteWarpCommand(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("deletewarp")
            .requires { it.sender.hasPermission("basicx.deletewarp") }
            .then(Commands.argument("warpName", StringArgumentType.string())
                .executes { ctx ->
                    val sender = ctx.source.sender
                    val warpName = StringArgumentType.getString(ctx, "warpName")
                    deleteWarp(sender, warpName)
                    Command.SINGLE_SUCCESS
                }
            )
            .build()
    }

    private fun deleteWarp(sender: CommandSender, warpName: String) {
        val player = sender as? Player ?: return

        WarpCommand.removeWarp(warpName)
        player.send("commands.deletewarp.success", mapOf("warpName" to warpName), default = "Warp {warpName} deleted")
    }
}
