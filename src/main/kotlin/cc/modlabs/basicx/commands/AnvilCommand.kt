package cc.modlabs.basicx.commands

import cc.modlabs.kpaper.extensions.sender
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player

class AnvilCommand : Command<CommandSourceStack> {

    override fun run(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.sender
        if (sender !is Player) {
            sender.sendMessage("This command can only be executed by a player.")
            return Command.SINGLE_SUCCESS
        }

        val player = sender as Player
        player.openAnvil(null, true)
        return Command.SINGLE_SUCCESS
    }

    companion object {
        fun register(): LiteralCommandNode<CommandSourceStack> {
            return Commands.literal("anvil")
                .executes(AnvilCommand())
                .build()
        }
    }
}
