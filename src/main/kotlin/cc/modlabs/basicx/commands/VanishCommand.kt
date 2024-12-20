package cc.modlabs.basicx.commands

import cc.modlabs.basicx.BasicX
import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

class VanishCommand {

    fun createVanishCommand(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("vanish")
            .requires { it.sender.hasPermission("basicx.vanish") }
            .then(Commands.argument("state", BoolArgumentType.bool())
                .executes { ctx -> executeVanish(ctx, BoolArgumentType.getBool(ctx, "state")) }
            )
            .then(Commands.argument("player", StringArgumentType.string())
                .requires { it.sender.hasPermission("basicx.vanish.others") }
                .then(Commands.argument("state", BoolArgumentType.bool())
                    .executes { ctx -> executeVanish(ctx, BoolArgumentType.getBool(ctx, "state"), StringArgumentType.getString(ctx, "player")) }
                )
            )
            .executes { ctx -> executeVanish(ctx, null) }
            .build()
    }

    private fun executeVanish(ctx: CommandContext<CommandSourceStack>, state: Boolean?, targetPlayerName: String? = null): Int {
        val sender = ctx.source.sender
        val targetPlayer: Player = if (targetPlayerName != null) {
            Bukkit.getPlayer(targetPlayerName) ?: run {
                sender.send("commands.vanish.player-not-found", mapOf("player" to targetPlayerName), default = "Player {player} not found.")
                return Command.SINGLE_SUCCESS
            }
        } else {
            if (sender is Player) sender else {
                sender.send("commands.vanish.console-usage", default = "Console must specify a player.")
                return Command.SINGLE_SUCCESS
            }
        }

        val vanishState = state ?: !targetPlayer.hasMetadata("vanished")
        if (vanishState) {
            targetPlayer.setMetadata("vanished", FixedMetadataValue(BasicX.instance, true))
            Bukkit.getOnlinePlayers().forEach { it.hidePlayer(BasicX.instance, targetPlayer) }
            targetPlayer.send("commands.vanish.enabled", default = "You are now vanished.")
        } else {
            targetPlayer.removeMetadata("vanished", BasicX.instance)
            Bukkit.getOnlinePlayers().forEach { it.showPlayer(BasicX.instance, targetPlayer) }
            targetPlayer.send("commands.vanish.disabled", default = "You are no longer vanished.")
        }

        return Command.SINGLE_SUCCESS
    }
}
