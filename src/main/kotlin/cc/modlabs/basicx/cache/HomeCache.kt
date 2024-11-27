package cc.modlabs.basicx.cache

import cc.modlabs.basicx.utils.FileConfig
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.UUID
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

object HomeCache {

    private val cacheLock: ReadWriteLock = ReentrantReadWriteLock()
    private var cache: MutableMap<UUID, MutableMap<String, Location>> = mutableMapOf()

    val homeAmount: Int
        get() = cache.values.sumOf { it.size }

    val homedPlayers: Int
        get() = cache.size

    fun getHome(playerUUID: UUID, homeName: String): Location? {
        cacheLock.readLock().lock()
        val playerHomes = cache[playerUUID]
        val location = playerHomes?.get(homeName)
        cacheLock.readLock().unlock()
        return location
    }

    fun getHomes(playerUUID: UUID): List<String> {
        cacheLock.readLock().lock()
        val playerHomes = cache[playerUUID]
        val homes = playerHomes?.keys?.toList() ?: emptyList()
        cacheLock.readLock().unlock()
        return homes
    }

    fun addHome(playerUUID: UUID, homeName: String, location: Location) {
        cacheLock.writeLock().lock()
        val playerHomes = cache.getOrPut(playerUUID) { mutableMapOf() }
        playerHomes[homeName] = location
        saveCache()
        cacheLock.writeLock().unlock()
    }

    fun removeHome(playerUUID: UUID, homeName: String) {
        cacheLock.writeLock().lock()
        val playerHomes = cache[playerUUID]
        playerHomes?.remove(homeName)
        if (playerHomes != null && playerHomes.isEmpty()) {
            cache.remove(playerUUID)
        }
        saveCache()
        cacheLock.writeLock().unlock()
    }

    fun loadCache() {
        cacheLock.writeLock().lock()
        cache = mutableMapOf()

        val homesFile = FileConfig("homes.yml")
        homesFile.getKeys(false).forEach { playerKey ->
            val playerUUID = UUID.fromString(playerKey)
            val playerHomes = mutableMapOf<String, Location>()
            homesFile.getConfigurationSection(playerKey)?.getKeys(false)?.forEach { homeKey ->
                val world = Bukkit.getWorld(homesFile.getString("$playerKey.$homeKey.world") ?: return@forEach)
                val x = homesFile.getDouble("$playerKey.$homeKey.x")
                val y = homesFile.getDouble("$playerKey.$homeKey.y")
                val z = homesFile.getDouble("$playerKey.$homeKey.z")
                val yaw = homesFile.getDouble("$playerKey.$homeKey.yaw").toFloat()
                val pitch = homesFile.getDouble("$playerKey.$homeKey.pitch").toFloat()
                val location = Location(world, x, y, z, yaw, pitch)
                playerHomes[homeKey] = location
            }
            cache[playerUUID] = playerHomes
        }

        cacheLock.writeLock().unlock()
    }

    private fun saveCache() {
        val homesFile = FileConfig("homes.yml")

        cache.forEach { (playerKey, playerHomes) ->
            playerHomes.forEach { (homeKey, location) ->
                homesFile["$playerKey.$homeKey.world"] = location.world?.name
                homesFile["$playerKey.$homeKey.x"] = location.x
                homesFile["$playerKey.$homeKey.y"] = location.y
                homesFile["$playerKey.$homeKey.z"] = location.z
                homesFile["$playerKey.$homeKey.yaw"] = location.yaw
                homesFile["$playerKey.$homeKey.pitch"] = location.pitch
            }
        }

        homesFile.saveConfig()
    }
}
