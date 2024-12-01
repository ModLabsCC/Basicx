package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.fruxz.stacked.extension.asStyledString
import dev.fruxz.stacked.text
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.Material
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

class ItemEditCommand {

    fun register(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("itemedit")
            .requires { it.sender.hasPermission("basicx.itemedit") }
            .then(Commands.literal("enchant")
                .then(Commands.argument("enchantment", ArgumentTypes.resource(RegistryKey.ENCHANTMENT))
                    .then(Commands.argument("level", IntegerArgumentType.integer(0))
                        .executes { ctx ->
                            val enchantment = ctx.getArgument<Enchantment>("enchantment", Enchantment::class.java)
                            enchantItem(ctx, enchantment, IntegerArgumentType.getInteger(ctx, "level"))
                        }
                    )
                    .suggests { _, builder ->

                        RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).forEach { enchantment ->
                            builder.suggest(enchantment.key.key)
                        }

                        builder.buildFuture()
                    }
                )
            )
            .then(Commands.literal("rename")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                    .executes { ctx -> renameItem(ctx, StringArgumentType.getString(ctx, "name")) }
                )
            )
            .then(Commands.literal("lore")
                .then(Commands.literal("add")
                    .then(Commands.argument("line", StringArgumentType.greedyString())
                        .executes { ctx -> addLoreLine(ctx, StringArgumentType.getString(ctx, "line")) }
                    )
                )
                .then(Commands.literal("remove")
                    .then(Commands.argument("line", IntegerArgumentType.integer(0))
                        .executes { ctx -> removeLoreLine(ctx, IntegerArgumentType.getInteger(ctx, "line")) }
                    )
                )
                .then(Commands.literal("clear")
                    .executes { ctx -> clearLore(ctx) }
                )
                .then(Commands.literal("set")
                    .then(Commands.argument("line", IntegerArgumentType.integer(0))
                        .then(Commands.argument("text", StringArgumentType.greedyString())
                            .executes { ctx -> setLoreLine(ctx, IntegerArgumentType.getInteger(ctx, "line"), StringArgumentType.getString(ctx, "text")) }
                        )
                        .suggests { ctx, builder ->

                            val player = ctx.source.sender as? Player ?: return@suggests builder.buildFuture()
                            val item = player.inventory.itemInMainHand
                            val meta = item.itemMeta ?: return@suggests builder.buildFuture()
                            val lore = meta.lore() ?: return@suggests builder.buildFuture()

                            for (i in 0 until lore.size) {
                                builder.suggest("$i", MessageComponentSerializer.message().serialize(text(lore[i].asStyledString)))
                            }
                            builder.buildFuture()
                        }
                    )
                )
            )
    }

    private fun addLoreLine(ctx: CommandContext<CommandSourceStack>, line: String): Int {
        val player = ctx.source.sender as? Player ?: return Command.SINGLE_SUCCESS
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta ?: return Command.SINGLE_SUCCESS

        val lore = meta.lore() ?: mutableListOf()
        lore.add(text(line))
        meta.lore(lore)
        item.itemMeta = meta

        player.send("commands.itemedit.lore-added", mapOf("line" to line), default = "Lore line added: {line}")

        return Command.SINGLE_SUCCESS
    }

    private fun removeLoreLine(ctx: CommandContext<CommandSourceStack>, line: Int): Int {
        val player = ctx.source.sender as? Player ?: return Command.SINGLE_SUCCESS
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta ?: return Command.SINGLE_SUCCESS


        val lore = meta.lore() ?: run {
            player.send("commands.itemedit.lore-empty", default = "Lore is empty")
            return Command.SINGLE_SUCCESS
        }
        if (line >= lore.size) {
            player.send("commands.itemedit.lore-line-out-of-bounds", mapOf("line" to line), default = "Lore line out of bounds: {line}")
            return Command.SINGLE_SUCCESS
        }
        lore.removeAt(line)
        meta.lore(lore)
        item.itemMeta = meta

        player.send("commands.itemedit.lore-removed", mapOf("line" to line), default = "Lore line removed: {line}")

        return Command.SINGLE_SUCCESS
    }

    private fun clearLore(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.source.sender as? Player ?: return Command.SINGLE_SUCCESS
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta ?: run {
            player.send("commands.itemedit.lore-empty", default = "Lore is empty")
            return Command.SINGLE_SUCCESS
        }

        val lore = meta.lore() ?: return Command.SINGLE_SUCCESS
        meta.lore(null)
        item.itemMeta = meta

        player.send("commands.itemedit.lore-cleared", default = "Lore cleared")

        return Command.SINGLE_SUCCESS
    }

    private fun setLoreLine(ctx: CommandContext<CommandSourceStack>, line: Int, text: String): Int {
        val player = ctx.source.sender as? Player ?: return Command.SINGLE_SUCCESS
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta ?: return Command.SINGLE_SUCCESS

        val lore = meta.lore() ?: run {
            player.send("commands.itemedit.lore-empty", default = "Lore is empty")
            return Command.SINGLE_SUCCESS
        }
        if (line >= lore.size) {
            player.send("commands.itemedit.lore-line-out-of-bounds", mapOf("line" to line), default = "Lore line out of bounds: {line}")
            return Command.SINGLE_SUCCESS
        }
        lore[line] = text(text)
        meta.lore(lore)
        item.itemMeta = meta

        player.send("commands.itemedit.lore-set", mapOf("line" to line, "text" to text), default = "Lore line {line} set to: {text}")

        return Command.SINGLE_SUCCESS
    }


    private fun enchantItem(ctx: CommandContext<CommandSourceStack>, enchantment: Enchantment, level: Int): Int {
        val player = ctx.source.sender as? Player ?: return Command.SINGLE_SUCCESS
        val item = player.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            player.send("commands.itemedit.no-item", default = "You must hold an item to enchant it.")
            return Command.SINGLE_SUCCESS
        }


        item.addUnsafeEnchantment(enchantment, level)
        player.send("commands.itemedit.enchanted", mapOf("enchantment" to enchantment.displayName(level).asStyledString), default = "Item enchanted with {enchantment}")
        return Command.SINGLE_SUCCESS
    }

    private fun renameItem(ctx: CommandContext<CommandSourceStack>, name: String): Int {
        val player = ctx.source.sender as? Player ?: return Command.SINGLE_SUCCESS
        val item = player.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            player.send("commands.itemedit.no-item", default = "You must hold an item to rename it.")
            return Command.SINGLE_SUCCESS
        }

        val meta = item.itemMeta
        meta?.displayName(text(name))
        item.itemMeta = meta

        player.send("commands.itemedit.renamed", mapOf("name" to name), default = "Item renamed to: {name}")
        return Command.SINGLE_SUCCESS
    }
}
