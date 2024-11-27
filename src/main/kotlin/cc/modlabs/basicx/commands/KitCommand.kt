package cc.modlabs.basicx.commands

import cc.modlabs.basicx.cache.KitCache
import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID

class KitCommand : Command<CommandSourceStack> {

    override fun run(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (sender !is Player) {
            sender.send("commands.kit.not-player", default = "Only players can use this command.")
            return Command.SINGLE_SUCCESS
        }

        val kitName = StringArgumentType.getString(context, "kit")
        val kit = KitCache.getKit(kitName)
        if (kit == null) {
            sender.send("commands.kit.not-found", mapOf("kit" to kitName), default = "Kit {kit} not found.")
            return Command.SINGLE_SUCCESS
        }

        if (!sender.hasPermission("basicx.kit.$kitName")) {
            sender.send("commands.kit.no-permission", mapOf("kit" to kitName), default = "You do not have permission to use the {kit} kit.")
            return Command.SINGLE_SUCCESS
        }

        val cooldownEnd = KitCache.getCooldown(sender.uniqueId, kitName)
        if (System.currentTimeMillis() < cooldownEnd) {
            val remainingTime = (cooldownEnd - System.currentTimeMillis()) / 1000
            sender.send("commands.kit.cooldown", mapOf("kit" to kitName, "time" to remainingTime), default = "You must wait {time} seconds before using the {kit} kit again.")
            return Command.SINGLE_SUCCESS
        }

        giveKit(sender, kit)
        KitCache.setCooldown(sender.uniqueId, kitName, System.currentTimeMillis() + 60000) // 1 minute cooldown
        sender.send("commands.kit.success", mapOf("kit" to kitName), default = "You have received the {kit} kit.")
        return Command.SINGLE_SUCCESS
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
                .then(Commands.literal("preview")
                    .then(Commands.argument("kit", StringArgumentType.string())
                        .executes { context ->
                            val sender = context.source.sender
                            if (sender !is Player) {
                                sender.send("commands.kit.not-player", default = "Only players can use this command.")
                                return@executes Command.SINGLE_SUCCESS
                            }

                            val kitName = StringArgumentType.getString(context, "kit")
                            val kit = KitCache.getKit(kitName)
                            if (kit == null) {
                                sender.send("commands.kit.not-found", mapOf("kit" to kitName), default = "Kit {kit} not found.")
                                return@executes Command.SINGLE_SUCCESS
                            }

                            sender.send("commands.kit.preview", mapOf("kit" to kitName), default = "Previewing the {kit} kit:")
                            for (item in kit) {
                                sender.send("commands.kit.preview-item", mapOf("item" to item.type.name, "amount" to item.amount), default = "{amount}x {item}")
                            }

                            Command.SINGLE_SUCCESS
                        }
                    )
                )
                .build()
        }
    }
}
