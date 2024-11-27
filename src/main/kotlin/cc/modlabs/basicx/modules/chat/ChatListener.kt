package cc.modlabs.basicx.modules.chat

import cc.modlabs.basicx.cache.MessageCache
import cc.modlabs.basicx.extensions.parsePlaceholders
import dev.fruxz.stacked.extension.asPlainString
import dev.fruxz.stacked.text
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatListener : Listener {

    private val chatFormat: String
        get() {
            return MessageCache.getMessage("chat.format")
        }

    @EventHandler
    fun onChat(event: AsyncChatEvent): Unit = with(event) {
        val messagePlain = message().asPlainString

        // Highlight player names in the chat message
        val highlightedMessage = highlightPlayerNames(messagePlain)

        var format =
            text(parsePlaceholders(chatFormat, player)).append(text(parsePlaceholders(highlightedMessage, player)))

        event.renderer { _, _, _, _ ->
            return@renderer format
        }
    }

    private fun highlightPlayerNames(message: String): String {
        val playerNames = Bukkit.getOnlinePlayers().map { it.name }
        var highlightedMessage = message
        playerNames.forEach { playerName ->
            highlightedMessage = highlightedMessage.replace(playerName, "<color:#f6e58d>$playerName</color>")
        }
        return highlightedMessage
    }
}