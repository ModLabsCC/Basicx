package cc.modlabs.basicx.managers

import cc.modlabs.basicx.BasicX
import com.google.common.reflect.ClassPath
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.logging.Level

object RegisterManager {

    private val logger
        get() = BasicX.instance.logger

    private fun loadListenersInPackage(packageName: String): List<Class<out Listener>> {
        try {
            val classLoader = BasicX.instance.javaClass.classLoader
            val allClasses = ClassPath.from(classLoader).allClasses
            val classes = mutableListOf<Class<out Listener>>()
            for (classInfo in allClasses) {
                if (!classInfo.name.startsWith("cc.modlabs.basicx")) continue
                if (classInfo.packageName.startsWith(packageName) && !classInfo.name.contains('$')) {
                    try {
                        val loadedClass = classInfo.load()
                        if (Listener::class.java.isAssignableFrom(loadedClass)) {
                            classes.add(loadedClass.asSubclass(Listener::class.java))
                        }
                    } catch (_: LinkageError) {
                        // An optional dependency for this class is unavailable.
                    }
                }
            }
            return classes
        } catch (exception: Exception) {
            logger.log(Level.SEVERE, "Failed to load classes", exception)
            return emptyList()
        }
    }

    fun registerListenersForModule(plugin: Plugin, module: String) {
        val listenerClasses = loadListenersInPackage("cc.modlabs.basicx.modules.$module")

        var amountListeners = 0
        listenerClasses.forEach {
            try {
                val listener = it.getDeclaredConstructor().newInstance()
                Bukkit.getPluginManager().registerEvents(listener, plugin)
                amountListeners++
            } catch (e: Exception) {
                logger.log(Level.SEVERE, "Failed to register listener: ${it.simpleName}", e)
            }
        }
        if (amountListeners == 0) return
        plugin.logger.info("Registered $amountListeners listeners for module $module")
    }
}
