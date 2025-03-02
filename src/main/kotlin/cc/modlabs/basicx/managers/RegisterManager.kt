package cc.modlabs.basicx.managers

import cc.modlabs.basicx.BasicX
import cc.modlabs.klassicx.extensions.getLogger
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
                        // Ignore, as this is not a class we need to load
                    }
                }
            }
            return classes
        } catch (exception: Exception) {
            logger.error("Failed to load classes", exception)
            return emptyList()
        }
    }

    fun registerListenersForModule(plugin: Plugin, module: String) {
        val listenerClasses = loadClassesInPackage("cc.modlabs.basicx.modules.${module}", Listener::class)

        var amountListeners = 0
        listenerClasses.forEach {
            try {
                val listener = it.primaryConstructor?.call() as Listener
                Bukkit.getPluginManager().registerEvents(listener, plugin)
                amountListeners++
            } catch (e: Exception) {
                logger.error("Failed to register listener: ${it.simpleName}", e)
            }
        }
        if (amountListeners == 0) return
        plugin.logger.info("Registered $amountListeners listeners for module $module")
    }
}
