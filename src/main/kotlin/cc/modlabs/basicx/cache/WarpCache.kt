package cc.modlabs.basicx.cache

import cc.modlabs.basicx.managers.FileConfig
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

object WarpCache {

    private val cacheLock: ReadWriteLock = ReentrantReadWriteLock()
    private var cache: MutableMap<String, Location> = mutableMapOf()

    val warpAmount: Int
        get() = cache.size

    fun getWarp(name: String): Location? {
        cacheLock.readLock().lock()
        val location = cache[name]
        cacheLock.readLock().unlock()
        return location
    }

    fun getWarps():List<String> {
        cacheLock.readLock().lock()
        val warps = cache.keys.toList()
        cacheLock.readLock().unlock()
        return warps
    }

    fun addWarp(name: String, location: Location) {
        cacheLock.writeLock().lock()
        cache[name] = location
        saveCache()
        cacheLock.writeLock().unlock()
    }

    fun removeWarp(name: String) {
        cacheLock.writeLock().lock()
        cache.remove(name)
        saveCache()
        cacheLock.writeLock().unlock()
    }

    fun loadCache() {
        cacheLock.writeLock().lock()
        cache = mutableMapOf()

        val warpsFile = FileConfig("warps.yml")
        warpsFile.getKeys(false).forEach { key ->
            val world = Bukkit.getWorld(warpsFile.getString("$key.world") ?: return@forEach)
            val x = warpsFile.getDouble("$key.x")
            val y = warpsFile.getDouble("$key.y")
            val z = warpsFile.getDouble("$key.z")
            val yaw = warpsFile.getDouble("$key.yaw").toFloat()
            val pitch = warpsFile.getDouble("$key.pitch").toFloat()
            val location = Location(world, x, y, z, yaw, pitch)
            cache[key] = location
        }

        cacheLock.writeLock().unlock()
    }

    private fun saveCache() {
        val warpsFile = FileConfig("warps.yml")

        cache.forEach { (key, location) ->
            warpsFile["$key.world"] = location.world?.name
            warpsFile["$key.x"] = location.x
            warpsFile["$key.y"] = location.y
            warpsFile["$key.z"] = location.z
            warpsFile["$key.yaw"] = location.yaw
            warpsFile["$key.pitch"] = location.pitch
        }

        warpsFile.saveConfig()
    }
}
