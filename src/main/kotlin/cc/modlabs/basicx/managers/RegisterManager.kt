package cc.modlabs.basicx.managers

import cc.modlabs.basicx.BasicX
import cc.modlabs.basicx.extensions.getLogger
import com.google.common.reflect.ClassPath
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object RegisterManager {

    private val logger = getLogger()

    private fun <T : Any> loadClassesInPackage(packageName: String, clazzType: KClass<T>): List<KClass<out T>> {
        try {
            val classLoader = BasicX.instance.javaClass.classLoader
            val allClasses = ClassPath.from(classLoader).allClasses
            val classes = mutableListOf<KClass<out T>>()
            for (classInfo in allClasses) {
                if (!classInfo.name.startsWith("cc.modlabs.basicx")) continue
                if (classInfo.packageName.startsWith(packageName) && !classInfo.name.contains('$')) {
                    try {
                        val loadedClass = classInfo.load().kotlin
                        if (clazzType.isInstance(loadedClass.javaObjectType.getDeclaredConstructor().newInstance())) {
                            classes.add(loadedClass as KClass<out T>)
                        }
                    } catch (_: Exception) {
                        // Ignore
                    }
                }
            }
            return classes
        } catch (exception: Exception) {
            logger.error("Failed to load classes", exception)
            return emptyList()
        }
    }

    /**
     * Registers listeners by iterating through a list of listener classes and registering them
     * with the Bukkit plugin manager.
     */
    fun registerListeners(plugin: Plugin) {
        val listenerClasses = loadClassesInPackage("cc.modlabs.basicx", Listener::class)

        logger.info("Found ${listenerClasses.size} listener classes to register:")
        listenerClasses.forEach { logger.info(it.simpleName) }

        var amountListeners = 0
        listenerClasses.forEach {
            val listener = it.primaryConstructor?.call() as Listener
            Bukkit.getPluginManager().registerEvents(listener, plugin)
            amountListeners++
        }
        logger.info("Registered $amountListeners listeners")
    }
}