package dev.steyn.kotlinloader.loader

import dev.steyn.kotlinloader.exception.ProtectedClassException
import java.net.URL
import java.net.URLClassLoader
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractPluginClassLoader(
        val pluginLoader: KotlinPluginLoader,
        urls: Array<URL>,
        parent: ClassLoader
) : URLClassLoader(urls, parent) {

    companion object {
        val PROTECTED = arrayOf(
                "org.bukkit.",
                "net.minecraft."
        )
    }

    val classes: MutableMap<String, Class<*>> = ConcurrentHashMap()


    open fun findClass(name: String, global: Boolean) : Class<*>? {
        fun byLocal(name: String): Class<*>? = classes[name]
        fun byGlobal(name: String): Class<*>? = pluginLoader.getClass(name)
        fun byParent() = super.findClass(name)
        val result = byLocal(name) ?: (if (global) byGlobal(name) else null) ?: byJar(name)
        ?: byParent()
        if (result != null) {
            pluginLoader.registerClass(name, result)
        }
        classes[name] = result
        return result
    }

    override fun findClass(name: String): Class<*> {
        if(isIllegal(name)) {
            throw ProtectedClassException(name)
        }
        return findClass(name, true) ?: throw ClassNotFoundException(name)
    }

    open fun byJar(name: String) : Class<*>? = null

    private fun isIllegal(name: String) : Boolean {
        for(p in PROTECTED) {
            if(name.startsWith(p)) {
                return true
            }
        }
        return false
    }

}