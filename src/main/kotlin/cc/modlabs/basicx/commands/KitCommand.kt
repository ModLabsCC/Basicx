package cc.modlabs.basicx.commands

import cc.modlabs.basicx.cache.KitCache
import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture

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
                .executes { context ->
                    val sender = context.source.sender

                    // list all kits
                    val kits = KitCache.getKits()
                    if (kits.isEmpty()) {
                        sender.send("commands.kit.list-empty", default = "There are no kits available.")
                        return@executes Command.SINGLE_SUCCESS
                    }

                    sender.send("commands.kit.list", default = "Available kits are:")
                    for (kit in kits) {
                        sender.send("commands.kit.list-entry", mapOf("kit" to kit), default = "» <yellow><click:run_command:'/kit {kit}'><hover:show_text:'Click to receive the {kit} kit'>{kit}</hover></click></yellow>")
                    }


                    Command.SINGLE_SUCCESS
                }
                .then(Commands.argument("kit", StringArgumentType.string())
                    .executes(KitCommand())
                    .suggests(::buildKitSuggest)
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
                        .suggests(::buildKitSuggest)
                    )
                )
                .then(Commands.literal("reload")
                    .requires { it.sender.hasPermission("basicx.kits.reload") }
                    .executes { context ->
                        val sender = context.source.sender
                        KitCache.loadCache()
                        sender.send("commands.kit.reload-success", default = "Kits reloaded.")
                        Command.SINGLE_SUCCESS
                    }
                )
                .build()
        }

        private fun buildKitSuggest(ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
            KitCache.getKits().forEach { kitName ->
                builder.suggest(kitName)
            }
            return builder.buildFuture()
        }
    }
}
