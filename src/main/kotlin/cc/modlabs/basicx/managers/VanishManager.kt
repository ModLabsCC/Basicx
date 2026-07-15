package cc.modlabs.basicx.managers

import cc.modlabs.basicx.BasicX
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object VanishManager {
    private val vanishedPlayers = ConcurrentHashMap.newKeySet<UUID>()

    fun isVanished(player: Player): Boolean = player.uniqueId in vanishedPlayers

    fun setVanished(player: Player, vanished: Boolean) {
        if (vanished) {
            vanishedPlayers += player.uniqueId
            Bukkit.getOnlinePlayers()
                .asSequence()
                .filterNot { it.uniqueId == player.uniqueId }
                .forEach { it.hideEntity(BasicX.instance, player) }
        } else {
            vanishedPlayers -= player.uniqueId
            Bukkit.getOnlinePlayers()
                .asSequence()
                .filterNot { it.uniqueId == player.uniqueId }
                .forEach { it.showEntity(BasicX.instance, player) }
        }
    }

    fun applyVisibilityFor(viewer: Player) {
        vanishedPlayers.asSequence()
            .mapNotNull(Bukkit::getPlayer)
            .filterNot { it.uniqueId == viewer.uniqueId }
            .forEach { viewer.hideEntity(BasicX.instance, it) }
    }

    fun clear() {
        vanishedPlayers.asSequence()
            .mapNotNull(Bukkit::getPlayer)
            .forEach { vanishedPlayer ->
                Bukkit.getOnlinePlayers()
                    .asSequence()
                    .filterNot { it.uniqueId == vanishedPlayer.uniqueId }
                    .forEach { viewer -> viewer.showEntity(BasicX.instance, vanishedPlayer) }
            }
        vanishedPlayers.clear()
    }
}
