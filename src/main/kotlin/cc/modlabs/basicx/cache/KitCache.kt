package cc.modlabs.basicx.cache

import cc.modlabs.basicx.BasicX
import cc.modlabs.basicx.managers.ConfigWriter
import cc.modlabs.basicx.managers.FileConfig
import cc.modlabs.basicx.util.isSafeIdentifier
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object KitCache {

    private val cacheLock: ReadWriteLock = ReentrantReadWriteLock()
    private var cache: MutableMap<String, List<ItemStack>> = mutableMapOf()
    private val cooldowns: MutableMap<UUID, MutableMap<String, Long>> = mutableMapOf()

    fun getKit(name: String): List<ItemStack>? =
        cacheLock.readLock().withLock { cache[name]?.map(ItemStack::clone) }

    fun getKits(): List<String> =
        cacheLock.readLock().withLock { cache.keys.sorted() }

    fun addKit(name: String, items: List<ItemStack>) {
        require(isSafeIdentifier(name)) { "Invalid kit name: $name" }
        val snapshot = cacheLock.writeLock().withLock {
            cache[name] = items.map(ItemStack::clone)
            snapshot()
        }
        saveCache(snapshot)
    }

    fun removeKit(name: String) {
        val snapshot = cacheLock.writeLock().withLock {
            cache.remove(name)
            snapshot()
        }
        saveCache(snapshot)
    }

    fun loadCache() {
        ConfigWriter.flush(FILE_NAME)
        val kitsFile = FileConfig(FILE_NAME)
        val kitNames = kitsFile.getKeys(false)
        val loaded = mutableMapOf<String, List<ItemStack>>()

        for (kitName in kitNames) {
            if (!isSafeIdentifier(kitName)) continue
            val items = mutableListOf<ItemStack>()

            val itemPaths = kitsFile.getConfigurationSection(kitName)?.getKeys(false) ?: continue

            for (itemPath in itemPaths) {
                val path = "$kitName.$itemPath"
                val item = kitsFile.getItemStack(path) ?: run {
                    val materialName = kitsFile.getString("$path.type") ?: return@run null
                    val material = Material.matchMaterial(materialName) ?: return@run null
                    ItemStack(material, kitsFile.getInt("$path.amount", 1).coerceAtLeast(1))
                }
                if (item != null) {
                    items.add(item.clone())
                }
            }

            loaded[kitName] = items
        }

        cacheLock.writeLock().withLock { cache = loaded }
        BasicX.instance.logger.info("Loaded ${loaded.size} kits")
    }

    private fun snapshot(): Map<String, List<ItemStack>> =
        cache.mapValues { (_, items) -> items.map(ItemStack::clone) }

    private fun saveCache(snapshot: Map<String, List<ItemStack>>) {
        val configuration = YamlConfiguration()
        snapshot.forEach { (kitName, items) ->
            items.forEachIndexed { index, itemStack ->
                configuration["$kitName.$index"] = itemStack
            }
        }
        ConfigWriter.schedule(FILE_NAME, configuration.saveToString())
    }

    fun getCooldown(playerUUID: UUID, kitName: String): Long =
        cacheLock.readLock().withLock { cooldowns[playerUUID]?.get(kitName) ?: 0L }

    fun setCooldown(playerUUID: UUID, kitName: String, cooldownEnd: Long) {
        cacheLock.writeLock().withLock {
            cooldowns.getOrPut(playerUUID) { mutableMapOf() }[kitName] = cooldownEnd
        }
    }

    private const val FILE_NAME = "kits.yml"

    fun flush() {
        ConfigWriter.flush(FILE_NAME)
    }
}
