package cc.modlabs.basicx.cache

import cc.modlabs.basicx.utils.FileConfig
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

object KitCache {

    private val cacheLock: ReadWriteLock = ReentrantReadWriteLock()
    private var cache: MutableMap<String, List<ItemStack>> = mutableMapOf()
    private val cooldowns: MutableMap<UUID, MutableMap<String, Long>> = mutableMapOf()

    fun getKit(name: String): List<ItemStack>? {
        cacheLock.readLock().lock()
        val kit = cache[name]
        cacheLock.readLock().unlock()
        return kit
    }

    fun addKit(name: String, items: List<ItemStack>) {
        cacheLock.writeLock().lock()
        cache[name] = items
        saveCache()
        cacheLock.writeLock().unlock()
    }

    fun removeKit(name: String) {
        cacheLock.writeLock().lock()
        cache.remove(name)
        saveCache()
        cacheLock.writeLock().unlock()
    }

    fun loadCache() {
        cacheLock.writeLock().lock()
        cache = mutableMapOf()

        val kitsFile = FileConfig("kits.yml")
        kitsFile.getKeys(false).forEach { kitName ->
            val items = mutableListOf<ItemStack>()
            kitsFile.getConfigurationSection(kitName)?.getKeys(false)?.forEach { itemKey ->
                val itemStack = kitsFile.getItemStack("$kitName.$itemKey")
                if (itemStack != null) {
                    items.add(itemStack)
                }
            }
            cache[kitName] = items
        }

        cacheLock.writeLock().unlock()
    }

    private fun saveCache() {
        val kitsFile = FileConfig("kits.yml")

        cache.forEach { (kitName, items) ->
            items.forEachIndexed { index, itemStack ->
                kitsFile["$kitName.$index"] = itemStack
            }
        }

        kitsFile.saveConfig()
    }

    fun getCooldown(playerUUID: UUID, kitName: String): Long {
        cacheLock.readLock().lock()
        val playerCooldowns = cooldowns[playerUUID]
        val cooldown = playerCooldowns?.get(kitName) ?: 0L
        cacheLock.readLock().unlock()
        return cooldown
    }

    fun setCooldown(playerUUID: UUID, kitName: String, cooldownEnd: Long) {
        cacheLock.writeLock().lock()
        val playerCooldowns = cooldowns.getOrPut(playerUUID) { mutableMapOf() }
        playerCooldowns[kitName] = cooldownEnd
        cacheLock.writeLock().unlock()
    }
}
