package cc.modlabs.basicx.commands

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TimeCommandTest {
    @Test
    fun `parses named clock and tick values`() {
        assertEquals(1_000, parseTimeInput("day"))
        assertEquals(6_000, parseTimeInput("noon"))
        assertEquals(6_000, parseTimeInput("12h"))
        assertEquals(1_000, parseTimeInput("25000"))
    }

    @Test
    fun `rejects malformed values`() {
        assertNull(parseTimeInput("tomorrow"))
        assertNull(parseTimeInput("xh"))
    }
}
