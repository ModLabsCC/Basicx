package cc.modlabs.basicx.commands

import cc.modlabs.basicx.extensions.send
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit

class WeatherCommand {

    fun createWeatherCommand(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("weather")
            .requires { it.sender.hasPermission("basicx.weather") }
            .then(Commands.argument("type", StringArgumentType.word())
                .suggests { _, builder ->
                    builder.suggest("clear")
                    builder.suggest("rain")
                    builder.suggest("thunder")
                    builder.buildFuture()
                }
                .executes { ctx -> setWeather(ctx) }
            )
            .build()
    }

    private fun setWeather(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val type = StringArgumentType.getString(ctx, "type")

        when (type) {
            "clear" -> {
                Bukkit.getWorlds().forEach { it.setStorm(false); it.isThundering = false }
                sender.send("commands.weather.set", mapOf("type" to "clear"), default = "Weather set to {type}")
            }
            "rain" -> {
                Bukkit.getWorlds().forEach { it.setStorm(true); it.isThundering = false }
                sender.send("commands.weather.set", mapOf("type" to "rain"), default = "Weather set to {type}")
            }
            "thunder" -> {
                Bukkit.getWorlds().forEach { it.setStorm(true); it.isThundering = true }
                sender.send("commands.weather.set", mapOf("type" to "thunder"), default = "Weather set to {type}")
            }
            else -> {
                sender.send("commands.weather.invalid", default = "Invalid weather type")
                return Command.SINGLE_SUCCESS
            }
        }

        return Command.SINGLE_SUCCESS
    }
}
