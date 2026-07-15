package cc.modlabs.basicx.cache

import cc.modlabs.basicx.managers.ConfigWriter
import cc.modlabs.basicx.managers.FileConfig
import cc.modlabs.basicx.util.isSafeIdentifier
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.util.UUID
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object HomeCache {

    private val cacheLock: ReadWriteLock = ReentrantReadWriteLock()
    private var cache: MutableMap<UUID, MutableMap<String, StoredLocation>> = mutableMapOf()

    val homeAmount: Int
        get() = cacheLock.readLock().withLock { cache.values.sumOf { it.size } }

    val homedPlayers: Int
        get() = cacheLock.readLock().withLock { cache.size }

    fun getHome(playerUUID: UUID, homeName: String): Location? =
        cacheLock.readLock().withLock { cache[playerUUID]?.get(homeName) }?.resolve()

    fun getHomes(playerUUID: UUID): List<String> =
        cacheLock.readLock().withLock { cache[playerUUID]?.keys?.sorted() ?: emptyList() }

    fun addHome(playerUUID: UUID, homeName: String, location: Location) {
        require(isSafeIdentifier(homeName)) { "Invalid home name: $homeName" }
        val snapshot = cacheLock.writeLock().withLock {
            cache.getOrPut(playerUUID) { mutableMapOf() }[homeName] = StoredLocation.from(location)
            snapshot()
        }
        saveCache(snapshot)
    }

    fun removeHome(playerUUID: UUID, homeName: String) {
        val snapshot = cacheLock.writeLock().withLock {
            val playerHomes = cache[playerUUID]
            playerHomes?.remove(homeName)
            if (playerHomes != null && playerHomes.isEmpty()) {
                cache.remove(playerUUID)
            }
            snapshot()
        }
        saveCache(snapshot)
    }

    fun loadCache() {
        ConfigWriter.flush(FILE_NAME)
        val homesFile = FileConfig("homes.yml")
        val loaded = mutableMapOf<UUID, MutableMap<String, StoredLocation>>()
        homesFile.getKeys(false).forEach { playerKey ->
            val playerUUID = runCatching { UUID.fromString(playerKey) }.getOrNull() ?: return@forEach
            val playerHomes = mutableMapOf<String, StoredLocation>()
            homesFile.getConfigurationSection(playerKey)?.getKeys(false)?.forEach { homeKey ->
                if (!isSafeIdentifier(homeKey)) return@forEach
                val worldName = homesFile.getString("$playerKey.$homeKey.world") ?: return@forEach
                val x = homesFile.getDouble("$playerKey.$homeKey.x")
                val y = homesFile.getDouble("$playerKey.$homeKey.y")
                val z = homesFile.getDouble("$playerKey.$homeKey.z")
                val yaw = homesFile.getDouble("$playerKey.$homeKey.yaw").toFloat()
                val pitch = homesFile.getDouble("$playerKey.$homeKey.pitch").toFloat()
                playerHomes[homeKey] = StoredLocation(worldName, x, y, z, yaw, pitch)
            }
            if (playerHomes.isNotEmpty()) loaded[playerUUID] = playerHomes
        }
        cacheLock.writeLock().withLock { cache = loaded }
    }

    private fun snapshot(): Map<UUID, Map<String, StoredLocation>> =
        cache.mapValues { (_, homes) -> homes.toMap() }

    private fun saveCache(snapshot: Map<UUID, Map<String, StoredLocation>>) {
        val configuration = YamlConfiguration()
        snapshot.forEach { (playerKey, playerHomes) ->
            playerHomes.forEach { (homeKey, location) ->
                configuration["$playerKey.$homeKey.world"] = location.worldName
                configuration["$playerKey.$homeKey.x"] = location.x
                configuration["$playerKey.$homeKey.y"] = location.y
                configuration["$playerKey.$homeKey.z"] = location.z
                configuration["$playerKey.$homeKey.yaw"] = location.yaw
                configuration["$playerKey.$homeKey.pitch"] = location.pitch
            }
        }
        ConfigWriter.schedule(FILE_NAME, configuration.saveToString())
    }

    private const val FILE_NAME = "homes.yml"

    fun flush() {
        ConfigWriter.flush(FILE_NAME)
    }
}
