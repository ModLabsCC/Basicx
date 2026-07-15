package cc.modlabs.basicx.managers

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object TeleportRequestManager {
    private const val REQUEST_TTL_MILLIS = 60_000L
    private val requests = ConcurrentHashMap<UUID, Request>()

    data class Request(val requester: UUID, val expiresAt: Long)

    fun create(requester: UUID, target: UUID, now: Long = System.currentTimeMillis()) {
        requests[target] = Request(requester, now + REQUEST_TTL_MILLIS)
    }

    fun accept(target: UUID, now: Long = System.currentTimeMillis()): UUID? {
        val request = requests.remove(target) ?: return null
        return request.requester.takeIf { request.expiresAt > now }
    }

    fun deny(target: UUID, now: Long = System.currentTimeMillis()): UUID? = accept(target, now)

    fun removePlayer(player: UUID) {
        requests.remove(player)
        requests.entries.removeIf { (_, request) -> request.requester == player }
    }

    fun clear() {
        requests.clear()
    }
}
