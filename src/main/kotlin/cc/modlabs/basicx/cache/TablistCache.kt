package cc.modlabs.basicx.cache

import cc.modlabs.klassicx.extensions.getLogger
import cc.modlabs.klassicx.extensions.to3DigitsReversed
import cc.modlabs.kpaper.coroutines.sync
import dev.fruxz.stacked.text
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import java.util.*

object TablistCache {

    private var luckPerms: LuckPerms? = null
    private var playerTeams = mutableMapOf<UUID, Team>()

    fun loadLuckPerms(luckPerms: LuckPerms) {
        getLogger().info("Loaded LuckPerms API")
        this.luckPerms = luckPerms
    }

    fun onPlayerJoin(player: Player) {
        if (luckPerms == null) return
        recalculatePlayer(player)
    }

    fun recalculatePlayer(player: Player) {
        deleteTeam(player)
        addPlayerToTeam(player)
    }

    fun updateTeam(team: Team, player: Player): Team {
        val prefix = getPlayerPrefix(player)
        sync {
            if (prefix.isNotEmpty()) {
                team.prefix(text(prefix))
            }
        }
        return team
    }

    fun deleteTeam(player: Player) {
        sync {
            player.scoreboard.getEntityTeam(player)?.unregister()
            val team = playerTeams[player.uniqueId] ?: return@sync
            team.unregister()
        }
    }

    private fun addPlayerToTeam(player: Player) {

        sync {
            val team = getOrCreateTeam(player)
            team.addPlayer(player)
            getLogger().info("Added ${player.name} to team ${team.name}")
        }
    }

    private fun getPlayerPrefix(player: Player): String {
        val user = luckPerms!!.getPlayerAdapter(Player::class.java).getUser(player)
        val groupName = user.primaryGroup
        val group = luckPerms!!.groupManager.getGroup(groupName)
        return group?.cachedData?.metaData?.prefix ?: ""
    }

    private fun getPlayerGroupWeight(player: Player): String {
        if (luckPerms == null) return "999"
        val user = luckPerms!!.getPlayerAdapter(Player::class.java).getUser(player)
        val groupName = user.primaryGroup
        val group = luckPerms!!.groupManager.getGroup(groupName) ?: return "999"
        return group.weight.to3DigitsReversed
    }

    private fun getOrCreateTeam(player: Player): Team {
        if (playerTeams.containsKey(player.uniqueId)) return updateTeam(playerTeams[player.uniqueId]!!, player)

        val groupWeight = getPlayerGroupWeight(player)
        val teamName = "${groupWeight}-${player.name}"
        val team = Bukkit.getScoreboardManager().mainScoreboard.getTeam(teamName)
        if (team != null) return updateTeam(team, player)

        val newTeam = Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam(teamName)
        return updateTeam(newTeam, player)
    }
}