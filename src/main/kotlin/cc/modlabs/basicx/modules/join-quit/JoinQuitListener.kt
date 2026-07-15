package cc.modlabs.basicx.modules.`join-quit`

import cc.modlabs.basicx.cache.MessageCache
import cc.modlabs.basicx.cache.OnlinePlayerCache
import cc.modlabs.basicx.cache.TablistCache
import cc.modlabs.basicx.managers.ModuleManager
import cc.modlabs.basicx.managers.TeleportRequestManager
import cc.modlabs.basicx.managers.VanishManager
import cc.modlabs.basicx.modules.BasicXModule
import dev.fruxz.stacked.text
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinQuitListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        OnlinePlayerCache.add(event.player.name)
        TablistCache.onPlayerJoin(event.player)
        VanishManager.applyVisibilityFor(event.player)
        if (!ModuleManager.isModuleEnabled(BasicXModule.JOIN_QUIT)) return

        val joinMessage = MessageCache.getMessage("commands.joinquit.join", event.player, default = "<#C4E538>+ <#dfe6e9>%luckperms_prefix%{displayname}")

        event.joinMessage(text(joinMessage))
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        OnlinePlayerCache.remove(event.player.name)
        TablistCache.deleteTeam(event.player)
        TeleportRequestManager.removePlayer(event.player.uniqueId)
        if (!ModuleManager.isModuleEnabled(BasicXModule.JOIN_QUIT)) return

        val quitMessage = MessageCache.getMessage("commands.joinquit.quit", event.player, default = "<#EA2027>- <#dfe6e9>%luckperms_prefix%{displayname}")

        event.quitMessage(text(quitMessage))
    }
}