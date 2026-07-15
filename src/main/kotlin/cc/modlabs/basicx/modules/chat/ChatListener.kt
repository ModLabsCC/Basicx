package cc.modlabs.basicx.modules.chat

import cc.modlabs.basicx.BasicX
import cc.modlabs.basicx.cache.MessageCache
import cc.modlabs.basicx.cache.OnlinePlayerCache
import cc.modlabs.basicx.extensions.parsePlaceholders
import cc.modlabs.basicx.managers.ModuleManager
import cc.modlabs.basicx.modules.BasicXModule
import dev.fruxz.stacked.text
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ChatListener : Listener {

    private val chatFormat: String
        get() {
            return MessageCache.getMessage("chat.format")
        }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent): Unit = with(event) {
        if (!ModuleManager.isModuleEnabled(BasicXModule.CHAT)) return

        val viewers = viewers().toList()
        val originalMessage = message()
        isCancelled = true
        BasicX.instance.server.scheduler.runTask(BasicX.instance, Runnable {
            val highlightedMessage = highlightPlayerNames(originalMessage)
            val formattedMessage = text(parsePlaceholders(chatFormat, player))
                .append(highlightedMessage)
            viewers.forEach { viewer -> viewer.sendMessage(formattedMessage) }
        })
    }

    private fun highlightPlayerNames(message: Component): Component {
        var highlightedMessage = message
        OnlinePlayerCache.names().forEach { playerName ->
            highlightedMessage = highlightedMessage.replaceText(
                TextReplacementConfig.builder()
                    .matchLiteral(playerName)
                    .replacement(Component.text(playerName, NamedTextColor.YELLOW))
                    .build(),
            )
        }
        return highlightedMessage
    }
}