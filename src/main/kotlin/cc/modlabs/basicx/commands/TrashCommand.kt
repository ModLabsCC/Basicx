package cc.modlabs.basicx.commands

import cc.modlabs.basicx.BasicX
import cc.modlabs.basicx.extensions.sendMessagePrefixed
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class TrashCommand : BasicXCommand() {

    override fun createCommand(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("trash")
            .requires { it.hasPermission("basicx.trash") }
            .executes { ctx -> openTrashGUI(ctx) }
            .build()
    }

    private fun openTrashGUI(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.source.sender as? Player ?: return Command.SINGLE_SUCCESS
        val trashInventory = Bukkit.createInventory(null, 27, "Trash")

        player.openInventory(trashInventory)
        player.sendMessagePrefixed("commands.trash.open", default = "<green>Trash GUI opened. Dispose of your items here.")

        return Command.SINGLE_SUCCESS
    }
}
