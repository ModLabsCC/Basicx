package cc.modlabs.basicx.modules

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class BasicXModuleTest {
    @Test
    fun `module keys are unique and match the shipped schema`() {
        val keys = BasicXModule.entries.map(BasicXModule::packageName)

        assertEquals(keys.size, keys.toSet().size)
        assertFalse("economy" in keys)
        assertFalse("kit" in keys)
        assertEquals(1, keys.count { it == "teleport" })
    }
}
