package cc.modlabs.basicx.managers

import java.nio.charset.StandardCharsets
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

object ConfigWriter {
    private const val SAVE_DELAY_MILLIS = 500L
    @Volatile
    private var executor = createExecutor()
    private val pendingContent = ConcurrentHashMap<String, String>()
    private val pendingTasks = ConcurrentHashMap<String, ScheduledFuture<*>>()
    private val logger = Logger.getLogger(ConfigWriter::class.java.name)

    @Synchronized
    fun schedule(fileName: String, content: String) {
        if (executor.isShutdown) executor = createExecutor()
        pendingContent[fileName] = content
        pendingTasks.remove(fileName)?.cancel(false)
        pendingTasks[fileName] = executor.schedule(
            {
                try {
                    writePending(fileName)
                } catch (exception: Exception) {
                    logger.log(Level.SEVERE, "Failed to save $fileName", exception)
                }
            },
            SAVE_DELAY_MILLIS,
            TimeUnit.MILLISECONDS,
        )
    }

    @Synchronized
    fun flush(fileName: String) {
        pendingTasks.remove(fileName)?.cancel(false)
        writePending(fileName)
    }

    @Synchronized
    fun flushAll() {
        pendingContent.keys.toList().forEach(::flush)
    }

    @Synchronized
    fun shutdown() {
        flushAll()
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)
    }

    private fun createExecutor() = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "BasicX-ConfigWriter").apply { isDaemon = true }
    }

    @Synchronized
    private fun writePending(fileName: String) {
        val content = pendingContent.remove(fileName) ?: return
        pendingTasks.remove(fileName)
        val target = FileConfig.resolve(fileName)
        val temporary = target.resolveSibling("${target.fileName}.tmp")
        Files.createDirectories(target.parent)
        Files.writeString(temporary, content, StandardCharsets.UTF_8)
        try {
            Files.move(
                temporary,
                target,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE,
            )
        } catch (_: AtomicMoveNotSupportedException) {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING)
        }
    }
}
