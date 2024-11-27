package cc.modlabs.basicx.cache

import cc.modlabs.basicx.extensions.getLogger
import cc.modlabs.basicx.utils.FileConfig
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

    fun getKits(): List<String> {
        cacheLock.readLock().lock()
        val kits = cache.keys.toList()
        cacheLock.readLock().unlock()
        return kits
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
        val kitNames = kitsFile.getKeys(false)

        for (kitName in kitNames) {
            val items = mutableListOf<ItemStack>()

            val itemPaths = kitsFile.getConfigurationSection(kitName)?.getKeys(false) ?: continue

            for (itemPath in itemPaths) {
                val item = kitsFile.getItemStack("$kitName.$itemPath")
                if (item != null) {
                    items.add(item)
                }
            }

            cache[kitName] = items
        }

        getLogger().info("Loaded ${cache.size} kits")
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
