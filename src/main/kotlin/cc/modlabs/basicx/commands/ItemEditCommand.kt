package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.sendMessagePrefixed
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta

class ItemEditCommand {

    fun register(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("itemedit")
            .requires { it.hasPermission("basicx.itemedit") }
            .then(Commands.literal("sign")
                .then(Commands.argument("name", StringArgumentType.string())
                    .executes { ctx -> signItem(ctx, StringArgumentType.getString(ctx, "name")) }
                )
            )
            .then(Commands.literal("enchant")
                .then(Commands.argument("enchantment", StringArgumentType.string())
                    .then(Commands.argument("level", StringArgumentType.integer())
                        .executes { ctx -> enchantItem(ctx, StringArgumentType.getString(ctx, "enchantment"), StringArgumentType.getInteger(ctx, "level")) }
                    )
                )
            )
            .then(Commands.literal("rename")
                .then(Commands.argument("name", StringArgumentType.string())
                    .executes { ctx -> renameItem(ctx, StringArgumentType.getString(ctx, "name")) }
                )
            )
    }

    private fun signItem(ctx: CommandContext<CommandSourceStack>, name: String): Int {
        val player = ctx.source.sender as? Player ?: return Command.SINGLE_SUCCESS
        val item = player.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            player.sendMessagePrefixed("commands.itemedit.no-item", default = "You must hold an item to sign it.")
            return Command.SINGLE_SUCCESS
        }

        val meta = item.itemMeta
        meta?.setDisplayName(name)
        item.itemMeta = meta

        player.sendMessagePrefixed("commands.itemedit.signed", mapOf("name" to name), default = "Item signed with name: $name")
        return Command.SINGLE_SUCCESS
    }

    private fun enchantItem(ctx: CommandContext<CommandSourceStack>, enchantment: String, level: Int): Int {
        val player = ctx.source.sender as? Player ?: return Command.SINGLE_SUCCESS
        val item = player.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            player.sendMessagePrefixed("commands.itemedit.no-item", default = "You must hold an item to enchant it.")
            return Command.SINGLE_SUCCESS
        }

        val enchant = Enchantment.getByName(enchantment.toUpperCase())
        if (enchant == null) {
            player.sendMessagePrefixed("commands.itemedit.invalid-enchantment", mapOf("enchantment" to enchantment), default = "Invalid enchantment: $enchantment")
            return Command.SINGLE_SUCCESS
        }

        item.addUnsafeEnchantment(enchant, level)
        player.sendMessagePrefixed("commands.itemedit.enchanted", mapOf("enchantment" to enchantment, "level" to level), default = "Item enchanted with $enchantment level $level")
        return Command.SINGLE_SUCCESS
    }

    private fun renameItem(ctx: CommandContext<CommandSourceStack>, name: String): Int {
        val player = ctx.source.sender as? Player ?: return Command.SINGLE_SUCCESS
        val item = player.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            player.sendMessagePrefixed("commands.itemedit.no-item", default = "You must hold an item to rename it.")
            return Command.SINGLE_SUCCESS
        }

        val meta = item.itemMeta
        meta?.setDisplayName(name)
        item.itemMeta = meta

        player.sendMessagePrefixed("commands.itemedit.renamed", mapOf("name" to name), default = "Item renamed to: $name")
        return Command.SINGLE_SUCCESS
    }
}
