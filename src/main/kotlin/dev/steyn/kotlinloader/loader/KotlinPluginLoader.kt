package dev.steyn.kotlinloader.loader

import dev.steyn.kotlinloader.api.KotlinPlugin
import dev.steyn.kotlinloader.debug
import dev.steyn.kotlinloader.exception.PluginFileMissingException
import dev.steyn.kotlinloader.exception.PluginNotKotlinPluginException
import dev.steyn.kotlinloader.jar.KotlinPluginClassLoader
import dev.steyn.kotlinloader.loader.reflect.HackedClassMap
import dev.steyn.kotlinloader.loader.reflect.LanguageScanner
import org.bukkit.Server
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginLoader
import org.bukkit.plugin.RegisteredListener
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.jar.JarFile
import java.util.logging.Level
import java.util.regex.Pattern

class KotlinPluginLoader(
        val server: Server
) : PluginLoader {

    private val loaders: MutableList<AbstractPluginClassLoader> = CopyOnWriteArrayList<AbstractPluginClassLoader>()
    private val classes: ConcurrentHashMap<String, Class<*>> = ConcurrentHashMap()


    init {
        KotlinInjector.hackedMap = HackedClassMap(KotlinInjector.javaMapField, this)
        KotlinInjector.javaMapField = KotlinInjector.hackedMap
        KotlinInjector.kotlinPluginLoader = this
    }


    override fun loadPlugin(file: File): Plugin {
        debug("Attempting to load plugin for file ${file.name}")
        if (!file.exists()) {
            throw PluginFileMissingException(file)
        }
        val desc = getPluginDescription(file)
        val scanner = LanguageScanner.createScanner(file, desc)
        if (scanner.isKotlinPlugin()) {
            return this.loadJarPlugin(file, desc)
        }
        return KotlinInjector.loader.loadPlugin(file)
    }


    private fun loadJarPlugin(file: File, desc: PluginDescriptionFile): Plugin {
        val parent = file.parentFile
        val dataFolder = File(parent, desc.name)
        val loader = KotlinPluginClassLoader(
                file = file, desc = desc, jar = JarFile(file), folder = dataFolder, pluginLoader = this
        )
        loaders.add(loader)
        return loader.plugin
    }

    override fun disablePlugin(plugin: Plugin) {
        debug("Attempting to disable ${plugin.name}")
        if (plugin !is KotlinPlugin) {
            throw PluginNotKotlinPluginException(plugin)
        }
        if (!plugin.enabled) {
            return
        }
        plugin.logger.info("Disabling ${plugin.description.name}..")

        val loader = plugin.javaClass.classLoader
        if (loader is AbstractPluginClassLoader) {
            loader.classes.keys.forEach {
                unregisterClass(it)
            }
        }
        plugin.enabled = false
    }

    override fun enablePlugin(plugin: Plugin) {
        debug("Attempting to enable ${plugin.name}")
        if (plugin !is KotlinPlugin) {
            throw PluginNotKotlinPluginException(plugin)
        }
        if (plugin.enabled) {
            return
        }
        val loader = plugin.javaClass.classLoader
        if (loader !is AbstractPluginClassLoader) {
            throw PluginNotKotlinPluginException(plugin)
        }
        if (!loaders.contains(loader)) {
            loaders.add(loader)
            server.logger.log(Level.WARNING, "Enabled plugin with unregistered PluginClassLoader ${plugin.getDescription().fullName}")
        }
        plugin.logger.info("Enabling ${plugin.description.fullName}..")
        try {
            plugin.enabled = true
        } catch (throwable: Throwable) {
            if (throwable is Error) {
                throw throwable
            }
            server.logger.log(Level.SEVERE, "Error occurred while enabling ${plugin.description.fullName}", throwable)
        }

    }

    fun getClass(name: String, skipHcm: Boolean): Class<*>? {
        var cached = classes[name]
        if (!skipHcm) {
            if (cached == null) {
                cached = KotlinInjector.hackedMap.getSuper(name) // see if the javapluginloader has our class
            }
        }
        if (cached == null) {
            for (loader in loaders) {
                try {
                    cached = loader.findClass(name, false)
                } catch (e: ClassNotFoundException) {
                } catch (e: ClassCastException) {}
                if (cached != null) {
                    break
                }
            }
        }
        return cached
    }

    fun getClass(name: String) = getClass(name, false)


    fun registerClass(name: String, clz: Class<*>) {
        if (!classes.containsKey(name)) {
            classes[name] = clz
            if (ConfigurationSerializable::class.java.isAssignableFrom(clz)) {
                val serializable: Class<out ConfigurationSerializable?> = clz.asSubclass(ConfigurationSerializable::class.java)
                ConfigurationSerialization.registerClass(serializable)
            }
        }
    }

    private fun unregisterClass(name: String) {
        val clz = classes.remove(name)
        if (clz != null && ConfigurationSerializable::class.java.isAssignableFrom(clz)) {
            val serializable: Class<out ConfigurationSerializable?> = clz.asSubclass(ConfigurationSerializable::class.java)
            ConfigurationSerialization.unregisterClass(serializable)
        }
    }

    override fun createRegisteredListeners(listener: Listener, plugin: Plugin): MutableMap<Class<out Event>, MutableSet<RegisteredListener>> =
            KotlinInjector.loader.createRegisteredListeners(listener
                    , plugin)

    override fun getPluginFileFilters(): Array<Pattern> = arrayOf(*KotlinInjector.loader.pluginFileFilters)

    override fun getPluginDescription(file: File): PluginDescriptionFile {
        return KotlinInjector.loader.getPluginDescription(file)
    }


    val plugins: List<KotlinPlugin>
        get() = loaders.asSequence().map { it.plugin }.toList()
}