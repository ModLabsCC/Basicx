package cc.modlabs.basicx.cache

import cc.modlabs.basicx.BasicX
import cc.modlabs.basicx.integrations.PlaceholderIntegration
import cc.modlabs.basicx.managers.FileConfig
import cc.modlabs.basicx.util.resolveMessageVariables
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object MessageCache {

    private val cacheLock: ReadWriteLock = ReentrantReadWriteLock()
    private var cache: Map<String, String> = mapOf()
    private val reportedMissingKeys = ConcurrentHashMap.newKeySet<String>()

    fun getMessage(
        key: String,
        commandSender: CommandSender = Bukkit.getConsoleSender(),
        placeholders: Map<String, Any> = emptyMap(),
        default: String = key,
    ): String {
        var message: String = cacheLock.readLock().withLock { cache[key] } ?: run {
            if (reportedMissingKeys.add(key)) {
                BasicX.instance.logger.warning("Missing message key '$key'; using its built-in default")
            }
            default
        }

        for ((placeholder, value) in placeholders) {
            message = message.replace(
                "{$placeholder}",
                MiniMessage.miniMessage().escapeTags(value.toString()),
            )
        }
        message = commandSender.replaceSenderPlaceholders(message)


        return message
    }

    fun loadCache() {
        val tempCache = mutableMapOf<String, String>()
        val messages = FileConfig("messages.yml")
        messages.getKeys(true).forEach {
            val message = messages.getString(it) ?: return@forEach
            tempCache[it] = message
        }

        cacheLock.writeLock().withLock {
            cache = resolveMessageVariables(tempCache)
        }
        reportedMissingKeys.clear()
    }

    // Add placeholders with start and end %
    private val placeholderAPIRegex = Regex("%([a-zA-Z0-9_]+)%")
    private fun CommandSender.replaceSenderPlaceholders(inputMessage: String): String {
        var message = inputMessage
        message = message.replace("{player}", this.name)

        if (this is Player) {
            message = message.replace(
                "{displayname}",
                PlainTextComponentSerializer.plainText().serialize(this.displayName()),
            )

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                message = PlaceholderIntegration.parse(this, message)
            }

            message = placeholderAPIRegex.replace(message, "")
        }
        return message
    }
}