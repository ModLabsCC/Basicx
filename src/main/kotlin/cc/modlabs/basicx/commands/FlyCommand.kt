package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit

class FlyCommand : Command<CommandSourceStack> {
    override fun run(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        val targetName = StringArgumentType.getString(context, "target")
        val enable = BoolArgumentType.getBool(context, "enable")

        val target = Bukkit.getPlayer(targetName)
        if (target == null) {
            sender.send("commands.fly.target-not-found", mapOf("target" to targetName), default = "Player {target} not found.")
            return Command.SINGLE_SUCCESS
        }

        target.allowFlight = enable
        target.isFlying = enable

        sender.send("commands.fly.success", mapOf("target" to targetName, "enable" to enable), default = "Fly mode for {target} set to {enable}.")
        return Command.SINGLE_SUCCESS
    }

    companion object {
        fun createFlyCommand(): LiteralCommandNode<CommandSourceStack> {
            return Commands.literal("fly")
                .then(Commands.argument("target", StringArgumentType.string())
                    .then(Commands.argument("enable", BoolArgumentType.bool())
                        .executes(FlyCommand())
                    )
                )
                .build()
        }
    }
}
