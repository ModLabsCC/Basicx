package cc.modlabs.basicx.integrations

import cc.modlabs.basicx.cache.TablistCache
import cc.modlabs.klassicx.extensions.to3DigitsReversed
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit

object LuckPermsIntegration {
    fun load() {
        val provider = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)
            ?: return
        val luckPerms = provider.provider
        val adapter = luckPerms.getPlayerAdapter(org.bukkit.entity.Player::class.java)
        TablistCache.configureLuckPerms(
            prefixProvider = { player ->
                val user = adapter.getUser(player)
                luckPerms.groupManager.getGroup(user.primaryGroup)
                    ?.cachedData
                    ?.metaData
                    ?.prefix
                    .orEmpty()
            },
            weightProvider = { player ->
                val user = adapter.getUser(player)
                luckPerms.groupManager.getGroup(user.primaryGroup)
                    ?.weight
                    ?.to3DigitsReversed
                    ?: "999"
            },
        )
    }
}
