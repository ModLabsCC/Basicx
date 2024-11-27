package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import dev.fruxz.stacked.text
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player


fun createTrashCommand(): LiteralCommandNode<CommandSourceStack> {
    return Commands.literal("trash")
        .requires { it.sender.hasPermission("basicx.trash") }
        .executes { ctx -> openTrashGUI(ctx) }
        .build()
}

private fun openTrashGUI(ctx: CommandContext<CommandSourceStack>): Int {
    val player = ctx.source.sender as? Player ?: return Command.SINGLE_SUCCESS
    val trashInventory = Bukkit.createInventory(null, 27, text("Trash"))

    player.openInventory(trashInventory)
    player.send("commands.trash.open", default = "<green>Trash GUI opened. Dispose of your items here.")

    return Command.SINGLE_SUCCESS
}

