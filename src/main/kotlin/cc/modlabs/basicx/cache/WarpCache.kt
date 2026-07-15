package cc.modlabs.basicx.cache

import cc.modlabs.basicx.managers.ConfigWriter
import cc.modlabs.basicx.managers.FileConfig
import cc.modlabs.basicx.util.isSafeIdentifier
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object WarpCache {

    private val cacheLock: ReadWriteLock = ReentrantReadWriteLock()
    private var cache: MutableMap<String, StoredLocation> = mutableMapOf()

    val warpAmount: Int
        get() = cacheLock.readLock().withLock { cache.size }

    fun getWarp(name: String): Location? =
        cacheLock.readLock().withLock { cache[name] }?.resolve()

    fun getWarps(): List<String> =
        cacheLock.readLock().withLock { cache.keys.sorted() }

    fun addWarp(name: String, location: Location) {
        require(isSafeIdentifier(name)) { "Invalid warp name: $name" }
        val snapshot = cacheLock.writeLock().withLock {
            cache[name] = StoredLocation.from(location)
            snapshot()
        }
        saveCache(snapshot)
    }

    fun removeWarp(name: String) {
        val snapshot = cacheLock.writeLock().withLock {
            cache.remove(name)
            snapshot()
        }
        saveCache(snapshot)
    }

    fun loadCache() {
        ConfigWriter.flush(FILE_NAME)
        val warpsFile = FileConfig("warps.yml")
        val loaded = mutableMapOf<String, StoredLocation>()
        warpsFile.getKeys(false).forEach { key ->
            if (!isSafeIdentifier(key)) return@forEach
            val worldName = warpsFile.getString("$key.world") ?: return@forEach
            val x = warpsFile.getDouble("$key.x")
            val y = warpsFile.getDouble("$key.y")
            val z = warpsFile.getDouble("$key.z")
            val yaw = warpsFile.getDouble("$key.yaw").toFloat()
            val pitch = warpsFile.getDouble("$key.pitch").toFloat()
            loaded[key] = StoredLocation(worldName, x, y, z, yaw, pitch)
        }
        cacheLock.writeLock().withLock { cache = loaded }
    }

    private fun snapshot(): Map<String, StoredLocation> = cache.toMap()

    private fun saveCache(snapshot: Map<String, StoredLocation>) {
        val configuration = YamlConfiguration()
        snapshot.forEach { (key, location) ->
            configuration["$key.world"] = location.worldName
            configuration["$key.x"] = location.x
            configuration["$key.y"] = location.y
            configuration["$key.z"] = location.z
            configuration["$key.yaw"] = location.yaw
            configuration["$key.pitch"] = location.pitch
        }
        ConfigWriter.schedule(FILE_NAME, configuration.saveToString())
    }

    private const val FILE_NAME = "warps.yml"

    fun flush() {
        ConfigWriter.flush(FILE_NAME)
    }
}
