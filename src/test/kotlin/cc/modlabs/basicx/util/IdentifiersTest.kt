package cc.modlabs.basicx.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class IdentifiersTest {
    @Test
    fun `accepts safe command identifiers`() {
        assertTrue(isSafeIdentifier("starter-kit_2"))
    }

    @Test
    fun `rejects MiniMessage and command injection characters`() {
        assertFalse(isSafeIdentifier("home' run_command:'/op user"))
        assertFalse(isSafeIdentifier("<click:run_command:'/op user'>"))
        assertFalse(isSafeIdentifier(""))
    }
}
