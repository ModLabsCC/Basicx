package cc.modlabs.basicx.commands

import cc.modlabs.basicx.managers.AnvilSessionManager
import cc.modlabs.basicx.modules.BasicXModule
import cc.modlabs.basicx.util.canUseModule
import cc.modlabs.kpaper.extensions.sender
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.MenuType

class AnvilCommand : Command<CommandSourceStack> {

    override fun run(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.sender
        if (sender !is Player) {
            sender.sendMessage("This command can only be executed by a player.")
            return Command.SINGLE_SUCCESS
        }

        AnvilSessionManager.open(sender.uniqueId)
        val view = MenuType.ANVIL.create(sender, Component.text("Anvil"))
        sender.openInventory(view)
        return Command.SINGLE_SUCCESS
    }

    companion object {
        fun register(): LiteralCommandNode<CommandSourceStack> {
            return Commands.literal("anvil")
                .requires { it.canUseModule(BasicXModule.ANVIL, "basicx.anvil") }
                .executes(AnvilCommand())
                .build()
        }
    }
}
