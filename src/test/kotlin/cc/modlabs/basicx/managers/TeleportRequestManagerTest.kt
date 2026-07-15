package cc.modlabs.basicx.managers

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.UUID

class TeleportRequestManagerTest {
    @AfterEach
    fun clearRequests() {
        TeleportRequestManager.clear()
    }

    @Test
    fun `accepts a live request only once`() {
        val requester = UUID.randomUUID()
        val target = UUID.randomUUID()
        TeleportRequestManager.create(requester, target, now = 1_000)

        assertEquals(requester, TeleportRequestManager.accept(target, now = 1_001))
        assertNull(TeleportRequestManager.accept(target, now = 1_002))
    }

    @Test
    fun `rejects expired requests and removes disconnected players`() {
        val requester = UUID.randomUUID()
        val target = UUID.randomUUID()
        TeleportRequestManager.create(requester, target, now = 1_000)
        assertNull(TeleportRequestManager.accept(target, now = 61_000))

        TeleportRequestManager.create(requester, target, now = 2_000)
        TeleportRequestManager.removePlayer(requester)
        assertNull(TeleportRequestManager.accept(target, now = 2_001))
    }
}
