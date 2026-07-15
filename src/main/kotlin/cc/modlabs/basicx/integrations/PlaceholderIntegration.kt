package cc.modlabs.basicx.integrations

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player

object PlaceholderIntegration {
    fun parse(player: Player, text: String): String =
        PlaceholderAPI.setPlaceholders(player, text)
}
