package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun createEconomyCommand(): LiteralCommandNode<CommandSourceStack> {
    return Commands.literal("economy")
        .requires { it.sender.hasPermission("basicx.economy") }
        .then(Commands.literal("balance")
            .executes { ctx ->
                val sender = ctx.source.sender
                if (sender is Player) {
                    val balance = getBalance(sender)
                    sender.send("commands.economy.balance", mapOf("balance" to balance), default = "Your balance is {balance}")
                } else {
                    sender.send("commands.economy.error", default = "This command can only be used by players.")
                }
                return@executes Command.SINGLE_SUCCESS
            }
        )
        .then(Commands.literal("pay")
            .then(Commands.argument("player", StringArgumentType.string())
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                    .executes { ctx ->
                        val sender = ctx.source.sender
                        if (sender is Player) {
                            val targetName = StringArgumentType.getString(ctx, "player")
                            val amount = IntegerArgumentType.getInteger(ctx, "amount")
                            val target = Bukkit.getPlayer(targetName)
                            if (target != null && target.isOnline) {
                                if (pay(sender, target, amount)) {
                                    sender.send("commands.economy.pay.success", mapOf("amount" to amount, "player" to target.name), default = "You paid {amount} to {player}")
                                    target.send("commands.economy.pay.received", mapOf("amount" to amount, "player" to sender.name), default = "You received {amount} from {player}")
                                } else {
                                    sender.send("commands.economy.pay.error", default = "You do not have enough balance.")
                                }
                            } else {
                                sender.send("commands.economy.pay.error", default = "Player not found or not online.")
                            }
                        } else {
                            sender.send("commands.economy.error", default = "This command can only be used by players.")
                        }
                        return@executes Command.SINGLE_SUCCESS
                    }
                )
            )
        )
        .build()
}

private fun getBalance(player: Player): Int {
    // Placeholder for actual balance retrieval logic
    return 1000
}

private fun pay(sender: Player, target: Player, amount: Int): Boolean {
    // Placeholder for actual payment logic
    return true
}
