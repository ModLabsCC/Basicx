package cc.modlabs.basicx.extensions

import cc.modlabs.basicx.cache.MessageCache
import dev.fruxz.stacked.text
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun Player.send(key: String, placeholders: Map<String, Any> = emptyMap<String, String>(), default: String = key) {
    this.sendMessage(text(MessageCache.getMessage(key, this, placeholders, default)))
}

fun CommandSender.send(key: String, placeholders: Map<String, Any> = emptyMap<String, String>(), default: String = key) = sendMessage(text(MessageCache.getMessage(key, this, placeholders, default)))