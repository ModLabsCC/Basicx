package cc.modlabs.basicx.managers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class ConfigWriterTest {
    @TempDir
    lateinit var temporaryDirectory: Path

    @Test
    fun `flush atomically replaces stale configuration content`() {
        FileConfig.configure(temporaryDirectory)
        val target = temporaryDirectory.resolve("warps.yml")
        Files.writeString(target, "stale: true\n")

        ConfigWriter.schedule("warps.yml", "current: true\n")
        ConfigWriter.flush("warps.yml")

        assertEquals("current: true\n", Files.readString(target))
    }
}
