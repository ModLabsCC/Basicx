package cc.modlabs.basicx.commands

import cc.modlabs.basicx.BasicX
import cc.modlabs.basicx.cache.MessageCache
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
import org.bukkit.inventory.ItemStack

class KitCommand : Command<CommandSourceStack> {

    override fun run(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (sender !is Player) {
            sender.sendMessagePrefixed("commands.kit.not-player", default = "Only players can use this command.")
            return Command.SINGLE_SUCCESS
        }

        val kitName = StringArgumentType.getString(context, "kit")
        val kit = getKit(kitName)
        if (kit == null) {
            sender.sendMessagePrefixed("commands.kit.not-found", mapOf("kit" to kitName), default = "Kit {kit} not found.")
            return Command.SINGLE_SUCCESS
        }

        giveKit(sender, kit)
        sender.sendMessagePrefixed("commands.kit.success", mapOf("kit" to kitName), default = "You have received the {kit} kit.")
        return Command.SINGLE_SUCCESS
    }

    private fun getKit(name: String): List<ItemStack>? {
        return when (name.toLowerCase()) {
            "starter" -> listOf(
                ItemStack(Material.IRON_SWORD),
                ItemStack(Material.IRON_PICKAXE),
                ItemStack(Material.IRON_AXE),
                ItemStack(Material.IRON_SHOVEL),
                ItemStack(Material.BREAD, 16)
            )
            else -> null
        }
    }

    private fun giveKit(player: Player, kit: List<ItemStack>) {
        val inventory = player.inventory
        for (item in kit) {
            inventory.addItem(item)
        }
    }

    companion object {
        fun createKitCommand(): LiteralCommandNode<CommandSourceStack> {
            return Commands.literal("kit")
                .then(Commands.argument("kit", StringArgumentType.string())
                    .executes(KitCommand())
                )
                .build()
        }
    }
}
