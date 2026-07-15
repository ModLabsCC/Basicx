package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.send
import cc.modlabs.basicx.modules.BasicXModule
import cc.modlabs.basicx.util.canUseModule
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player

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
                .requires { it.canUseModule(BasicXModule.FLY, "basicx.fly") }
                .executes { context ->
                    val player = context.source.sender as? Player ?: run {
                        context.source.sender.send(
                            "commands.fly.not-player",
                            default = "Only players can use this command without a target.",
                        )
                        return@executes Command.SINGLE_SUCCESS
                    }
                    player.allowFlight = !player.allowFlight
                    player.isFlying = player.allowFlight
                    player.send(
                        "commands.fly.success",
                        mapOf("target" to player.name, "enable" to player.allowFlight),
                        default = "Fly mode for {target} set to {enable}.",
                    )
                    Command.SINGLE_SUCCESS
                }
                .then(Commands.argument("target", StringArgumentType.string())
                    .requires { it.sender.hasPermission("basicx.fly.others") }
                    .then(Commands.argument("enable", BoolArgumentType.bool())
                        .executes(FlyCommand())
                    )
                )
                .build()
        }
    }
}
