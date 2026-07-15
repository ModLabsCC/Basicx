package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.send
import cc.modlabs.basicx.modules.BasicXModule
import cc.modlabs.basicx.util.canUseModule
import cc.modlabs.basicx.util.isSafeIdentifier
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CreateWarpCommand {

    fun createWarpCommand(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("createwarp")
            .requires { it.canUseModule(BasicXModule.WARP, "basicx.createwarp") }
            .then(Commands.argument("warpName", StringArgumentType.string())
                .executes { ctx ->
                    val sender = ctx.source.sender
                    val warpName = StringArgumentType.getString(ctx, "warpName")
                    if (!isSafeIdentifier(warpName)) {
                        sender.send(
                            "commands.createwarp.invalid-name",
                            default = "Warp names may only contain letters, numbers, underscores, and hyphens.",
                        )
                        return@executes Command.SINGLE_SUCCESS
                    }
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
        player.send("commands.createwarp.success", mapOf("warpName" to warpName), default = "Warp {warpName} created")
    }
}
