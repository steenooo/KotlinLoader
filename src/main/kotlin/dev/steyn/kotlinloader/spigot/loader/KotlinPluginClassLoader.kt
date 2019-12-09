package dev.steyn.kotlinloader.spigot.loader

import com.google.common.io.ByteStreams
import dev.steyn.kotlinloader.spigot.KotlinLoader
import dev.steyn.kotlinloader.spigot.api.KotlinPlugin
import dev.steyn.kotlinloader.spigot.exception.InvalidPluginException
import org.bukkit.plugin.PluginDescriptionFile
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.security.CodeSource
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile
import java.util.jar.Manifest

class KotlinPluginClassLoader(
        val pluginLoader: KotlinPluginLoader,
        val file: File,
        val folder: File,
        parent: ClassLoader = KotlinLoader::class.java.classLoader,
        val desc: PluginDescriptionFile,
        private val jar: JarFile,
        private val manifest: Manifest? = jar.manifest,
        val classes: MutableMap<String, Class<*>> = ConcurrentHashMap(),
        val url: URL = file.toURI().toURL()
) : URLClassLoader(arrayOf(url), parent) {

    companion object {
        init {
            ClassLoader.registerAsParallelCapable()
        }
    }

    val plugin =
            try {
                Class.forName(desc.main, true, this) as Class<out KotlinPlugin>
            } catch (ex: ClassNotFoundException) {
                throw InvalidPluginException("Unable to find main class", ex)
            } catch (ex: ClassCastException) {
                throw InvalidPluginException("${desc.main} does not extend KotlinPlugin", ex)
            }.let {
                it.kotlin.objectInstance ?: try {
                    it.getDeclaredConstructor().newInstance()
                } catch (ex: IllegalAccessException) {
                    throw InvalidPluginException("Unable to find a public constructor", ex)
                } catch (ex: InstantiationException) {
                    throw InvalidPluginException("Unable to instantiate ${desc.main}", ex)
            }
            }

    init {
        plugin.init(file, folder, this, pluginLoader, desc, pluginLoader.server)
    }

    public override fun findClass(name: String): Class<*> {
        if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
            this.plugin.logger.warning("Tried to find a class that should not be searched after by us $name")
            throw ClassNotFoundException(name)
        }
        return findClass(name, true) ?: throw ClassNotFoundException(name)
    }

    internal fun findClass(name: String, global: Boolean): Class<*>? {
        fun byLocal(name: String): Class<*>? = classes[name]
        fun byGlobal(name: String): Class<*>? = pluginLoader.getClass(name)
        fun byParent() = super.findClass(name)
        val result =  byLocal(name) ?: (if (global) byGlobal(name) else null) ?: byJar(name) ?: byParent()
        if(result != null) {
            pluginLoader.registerClass(name, result)
        }
        classes[name] = result
        return result
    }

    private fun byJar(name: String): Class<*>? {
        try {
            val path = name.replace('.', '/') + "class"
            val entry = this.jar.getJarEntry(name)
            if (entry != null) {
                var bytes = jar.getInputStream(entry).use {
                    ByteStreams.toByteArray(it)
                }
                bytes = pluginLoader.server.unsafe.processClass(desc, path, bytes)

                val dot = name.lastIndexOf('.')
                if (dot != -1) {
                    val pkgName = name.substring(0, dot)
                    if (getPackage(pkgName) == null) {
                        try {
                            if (manifest != null) {
                                definePackage(pkgName, manifest, url)
                            } else {
                                definePackage(pkgName, null, null, null, null, null, null, null)
                            }
                        } catch (ex: IllegalArgumentException) {
                            checkNotNull(getPackage(pkgName)) { "Cannot find package $pkgName" }
                        }
                    }
                }
                val signers = entry.codeSigners
                val source = CodeSource(url, signers)
                return defineClass(name, bytes, 0, bytes.size, source)
            }
            return null
        } catch(e: Exception) {
            throw e
        }
    }

}