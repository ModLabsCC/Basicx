package cc.modlabs.basicx.managers

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object AnvilSessionManager {
    private val players = ConcurrentHashMap.newKeySet<UUID>()

    fun open(player: UUID) {
        players += player
    }

    fun isOpen(player: UUID): Boolean = player in players

    fun close(player: UUID) {
        players -= player
    }

    fun clear() {
        players.clear()
    }
}
