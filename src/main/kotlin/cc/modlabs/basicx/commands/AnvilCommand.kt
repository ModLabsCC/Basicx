package cc.modlabs.basicx.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class AnvilCommand : Command<CommandSourceStack> {

    override fun run(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (sender !is Player) {
            sender.sendMessage("This command can only be executed by a player.")
            return Command.SINGLE_SUCCESS
        }

        val player = sender as Player
        val inventory: Inventory = Bukkit.createInventory(player, 9, "Anvil")

        player.openInventory(inventory)
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
