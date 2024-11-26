package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.sendMessagePrefixed
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class HealCommand : Command<CommandSourceStack> {

    override fun run(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        val targetName = StringArgumentType.getString(context, "target")
        val target = Bukkit.getPlayer(targetName)

        if (target == null) {
            sender.sendMessagePrefixed("commands.heal.target-not-found", mapOf("target" to targetName), default = "Player {target} not found.")
            return Command.SINGLE_SUCCESS
        }

        target.health = target.maxHealth
        target.sendMessagePrefixed("commands.heal.success", default = "You have been healed.")
        sender.sendMessagePrefixed("commands.heal.sender-success", mapOf("target" to targetName), default = "You have healed {target}.")

        return Command.SINGLE_SUCCESS
    }

    companion object {
        fun createHealCommand(): LiteralCommandNode<CommandSourceStack> {
            return Commands.literal("heal")
                .requires { it.sender.hasPermission("basicx.heal") }
                .then(Commands.argument("target", StringArgumentType.string())
                    .executes(HealCommand())
                )
                .build()
        }
    }
}
