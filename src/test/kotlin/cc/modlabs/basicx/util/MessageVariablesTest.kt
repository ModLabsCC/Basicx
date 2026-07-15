package cc.modlabs.basicx.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MessageVariablesTest {
    @Test
    fun `resolves nested variables`() {
        val values = mapOf(
            "variables.prefix" to "<green>BasicX</green>",
            "variables.header" to "\${{variables.prefix}} >",
            "message" to "\${{variables.header}} Ready",
        )

        assertEquals(
            "<green>BasicX</green> > Ready",
            resolveMessageVariables(values).getValue("message"),
        )
    }

    @Test
    fun `leaves missing and cyclic variables stable`() {
        val values = mapOf(
            "missing" to "\${{variables.unknown}}",
            "a" to "\${{b}}",
            "b" to "\${{a}}",
        )

        val resolved = resolveMessageVariables(values)
        assertEquals("\${{variables.unknown}}", resolved.getValue("missing"))
        assertTrue(resolved.getValue("a") == "\${{a}}" || resolved.getValue("a") == "\${{b}}")
    }
}
