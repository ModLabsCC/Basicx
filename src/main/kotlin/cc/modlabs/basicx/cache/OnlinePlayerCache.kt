package cc.modlabs.basicx.cache

import java.util.concurrent.ConcurrentHashMap

object OnlinePlayerCache {
    private val names = ConcurrentHashMap.newKeySet<String>()

    fun add(name: String) {
        names += name
    }

    fun remove(name: String) {
        names -= name
    }

    fun names(): Set<String> = names.toSet()

    fun clear() {
        names.clear()
    }
}
