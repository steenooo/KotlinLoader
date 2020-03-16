package dev.steyn.kotlinloader.loader

import dev.steyn.kotlinloader.api.KotlinPlugin
import dev.steyn.kotlinloader.exception.ProtectedClassException
import java.net.URL
import java.net.URLClassLoader
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractPluginClassLoader(
        val pluginLoader: KotlinPluginLoader,
        urls: Array<URL>,
        private val _parent: ClassLoader
) : URLClassLoader(urls, null) {

    companion object {
        val PROTECTED = arrayOf(
                "org.bukkit.",
                "net.minecraft.",
                "dev.steyn.kotlinloader.api.event.Event"
        )
    }

    val classes: MutableMap<String, Class<*>> = ConcurrentHashMap()


    override fun loadClass(name: String?, resolve: Boolean): Class<*> {
        return discard<ClassNotFoundException> { super.loadClass(name, resolve) }
                ?: discard<ClassNotFoundException> { findClass(name!!, true) }
                ?: discard<ClassNotFoundException> {
                    discard<ClassCastException> {
                        _parent.loadClass(name)
                    }
                } ?: throw ClassNotFoundException(name)
    }


    private inline fun <reified T : Throwable> discard(handle: () -> Class<*>?): Class<*>? =
            try {
                handle()
            } catch (e: Exception) {
                if (e !is T) {
                    throw e
                }
                null
            }


    open fun findClass(name: String, global: Boolean): Class<*>? {
        val result = classes[name] ?: (if (global) pluginLoader.getClass(name) else null)
        ?: byJar(name)
        ?: super.findClass(name)
        if (result != null) {
            pluginLoader.registerClass(name, result)
        }
        classes[name] = result
        return result
    }

    override fun findClass(name: String): Class<*>? {
        if (isIllegal(name)) {
            throw ProtectedClassException(name)
        }
        return null
    }

    open fun byJar(name: String): Class<*>? = null

    private fun isIllegal(name: String): Boolean {
        for (p in PROTECTED) {
            if (name.startsWith(p)) {
                return true
            }
        }
        return false
    }

    abstract val plugin: KotlinPlugin

}