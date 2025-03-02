package cc.modlabs.basicx.modules.`join-quit`

import cc.modlabs.basicx.cache.MessageCache
import cc.modlabs.basicx.cache.TablistCache
import dev.fruxz.stacked.text
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinQuitListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        TablistCache.onPlayerJoin(event.player)
        val joinMessage = MessageCache.getMessage("commands.joinquit.join", event.player, default = "<#C4E538>+ <#dfe6e9>%luckperms_prefix%{displayname}")

        event.joinMessage(text(joinMessage))
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val quitMessage = MessageCache.getMessage("commands.joinquit.quit", event.player, default = "<#EA2027>- <#dfe6e9>%luckperms_prefix%{displayname}")

        event.quitMessage(text(quitMessage))
    }
}