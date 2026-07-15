package cc.modlabs.basicx.cache

import org.bukkit.Bukkit
import org.bukkit.Location

data class StoredLocation(
    val worldName: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
) {
    fun resolve(): Location? {
        val world = Bukkit.getWorld(worldName) ?: return null
        return Location(world, x, y, z, yaw, pitch)
    }

    companion object {
        fun from(location: Location): StoredLocation {
            val worldName = requireNotNull(location.world) {
                "Cannot store a location without a world"
            }.name
            return StoredLocation(
                worldName = worldName,
                x = location.x,
                y = location.y,
                z = location.z,
                yaw = location.yaw,
                pitch = location.pitch,
            )
        }
    }
}
