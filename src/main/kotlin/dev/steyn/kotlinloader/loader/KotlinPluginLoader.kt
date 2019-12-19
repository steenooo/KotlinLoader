package dev.steyn.kotlinloader.loader

import dev.steyn.kotlinloader.KotlinPlugin
import dev.steyn.kotlinloader.desc.KotlinPluginDescription
import dev.steyn.kotlinloader.desc.asKotlin
import dev.steyn.kotlinloader.exception.InvalidPluginException
import dev.steyn.kotlinloader.exception.PluginFileMissingException
import dev.steyn.kotlinloader.exception.PluginNotKotlinPluginException
import dev.steyn.kotlinloader.kts.KtsPluginClassLoader
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
    private val ktsFiles = ConcurrentHashMap<File, KtsPluginClassLoader>()


    init {
        KotlinInjector.hackedMap = HackedClassMap(KotlinInjector.javaMapField, this)
        KotlinInjector.javaMapField = KotlinInjector.hackedMap
    }


    override fun loadPlugin(file: File): Plugin {
        if (!file.exists()) {
            throw PluginFileMissingException(file)
        }
        if(file.name.endsWith(".kts")) {
            val x = ktsFiles[file] ?: throw InvalidPluginException("Plugin did not load.")
            x.init()
            return x.plugin
        }
        val desc = getPluginDescription(file).asKotlin()
        val scanner = LanguageScanner.createScanner(file, desc)
        if (scanner.isKotlinPlugin()) {
            return this.loadJarPlugin(file, desc)
        }
        return KotlinInjector.loader.loadPlugin(file)
    }


    private fun loadJarPlugin(file: File, desc: KotlinPluginDescription): Plugin {
        val parent = file.parentFile
        val dataFolder = File(parent, desc.name)
        val loader = KotlinPluginClassLoader(
                file = file, desc = desc, jar = JarFile(file), folder = dataFolder, pluginLoader = this
        )
        loaders.add(loader)
        return loader.plugin
    }

    override fun disablePlugin(plugin: Plugin) {
        if (plugin !is KotlinPlugin) {
            throw PluginNotKotlinPluginException(plugin)
        }
        if (!plugin.enabled) {
            return
        }
        plugin.logger.info("Disabling ${plugin.description.name}..")

        val loader = plugin.javaClass.classLoader
        if (loader is KotlinPluginClassLoader) {
            loader.classes.keys.forEach {
                unregisterClass(it)
            }
        }

        plugin.enabled = false
    }

    override fun enablePlugin(plugin: Plugin) {
        if (plugin !is KotlinPlugin) {
            throw PluginNotKotlinPluginException(plugin)
        }
        if (plugin.enabled) {
            return
        }
        val loader = plugin.javaClass.classLoader
        if (loader !is KotlinPluginClassLoader) {
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
                }
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

    override fun getPluginFileFilters(): Array<Pattern> = arrayOf(*KotlinInjector.loader.pluginFileFilters, Pattern.compile("\\.kts$"))

    override fun getPluginDescription(file: File) : PluginDescriptionFile{
        if(file.name.endsWith(".kts")) {
            return (ktsFiles[file] ?: let {
                val loader = KtsPluginClassLoader(this, javaClass.classLoader, file, server)
                ktsFiles[file] = loader
                loader
            }).description.bukkit
        }
        return KotlinInjector.loader.getPluginDescription(file)
    }
}