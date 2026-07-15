package cc.modlabs.basicx.cache

import cc.modlabs.basicx.BasicX
import dev.fruxz.stacked.text
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import java.util.*

object TablistCache {

    private var prefixProvider: ((Player) -> String)? = null
    private var weightProvider: ((Player) -> String)? = null
    private var playerTeams = mutableMapOf<UUID, Team>()

    fun configureLuckPerms(
        prefixProvider: (Player) -> String,
        weightProvider: (Player) -> String,
    ) {
        BasicX.instance.logger.info("Loaded LuckPerms API")
        this.prefixProvider = prefixProvider
        this.weightProvider = weightProvider
    }

    fun onPlayerJoin(player: Player) {
        if (prefixProvider == null) return
        recalculatePlayer(player)
    }

    fun recalculatePlayer(player: Player) {
        deleteTeam(player)
        addPlayerToTeam(player)
    }

    fun updateTeam(team: Team, player: Player): Team {
        val prefix = getPlayerPrefix(player)
        if (prefix.isNotEmpty()) {
            team.prefix(text(prefix))
        }
        return team
    }

    fun deleteTeam(player: Player) {
        val team = playerTeams.remove(player.uniqueId) ?: return
        team.removeEntry(player.name)
        team.unregister()
    }

    private fun addPlayerToTeam(player: Player) {
        val team = getOrCreateTeam(player)
        team.addEntry(player.name)
        playerTeams[player.uniqueId] = team
    }

    private fun getPlayerPrefix(player: Player): String {
        return prefixProvider?.invoke(player).orEmpty()
    }

    private fun getPlayerGroupWeight(player: Player): String {
        return weightProvider?.invoke(player) ?: "999"
    }

    private fun getOrCreateTeam(player: Player): Team {
        if (playerTeams.containsKey(player.uniqueId)) return updateTeam(playerTeams[player.uniqueId]!!, player)

        val groupWeight = getPlayerGroupWeight(player)
        val teamName = "${groupWeight}_${player.uniqueId.toString().take(8)}"
        val team = Bukkit.getScoreboardManager().mainScoreboard.getTeam(teamName)
        if (team != null) {
            playerTeams[player.uniqueId] = team
            return updateTeam(team, player)
        }

        val newTeam = Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam(teamName)
        playerTeams[player.uniqueId] = newTeam
        return updateTeam(newTeam, player)
    }

    fun clear() {
        playerTeams.values.toSet().forEach { team ->
            runCatching(team::unregister)
        }
        playerTeams.clear()
        prefixProvider = null
        weightProvider = null
    }
}