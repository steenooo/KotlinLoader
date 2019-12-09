package dev.steyn.kotlinloader.spigot.loader

import dev.steyn.kotlinloader.common.makeMutable
import dev.steyn.kotlinloader.spigot.api.KotlinPlugin
import dev.steyn.kotlinloader.spigot.exception.InjectException
import dev.steyn.kotlinloader.spigot.exception.PluginFileMissingException
import dev.steyn.kotlinloader.spigot.exception.PluginNotKotlinPluginException
import dev.steyn.kotlinloader.spigot.loader.reflect.HackedClassMap
import dev.steyn.kotlinloader.spigot.loader.reflect.LanguageScanner
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.*
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.jar.JarFile
import java.util.logging.Level
import java.util.regex.Pattern

class KotlinPluginLoader(
        val server: Server
) : PluginLoader {

    private val loaders: MutableList<KotlinPluginClassLoader> = CopyOnWriteArrayList<KotlinPluginClassLoader>()
    private val classes: ConcurrentHashMap<String, Class<*>> = ConcurrentHashMap()
    private  val hackedClassMap: HackedClassMap
    private  val default: JavaPluginLoader
    val transformers = listOf<ClassHandler>()
    
    init {
        val manager = Bukkit.getPluginManager()
        if (manager !is SimplePluginManager) throw Exception("Invalid PluginManager.")

        val associationField = manager.javaClass.getDeclaredField("fileAssociations")
        associationField.isAccessible = true
        val map = associationField.get(manager) as MutableMap<Pattern, PluginLoader>
        var _default: JavaPluginLoader? = null
        val iter = map.iterator()

        while(iter.hasNext()) {
            val curr = iter.next()
            val currVal = curr.value
            if(currVal is JavaPluginLoader) {
                _default = currVal
            }
        }

        default = _default ?: throw InjectException("Unable to find JavaPluginLoader")
        val field = JavaPluginLoader::class.java.getDeclaredField("classes")
        field.isAccessible = true
        val _classes = field.get(default) as ConcurrentHashMap<String, Class<*>>
        hackedClassMap = HackedClassMap(_classes, this)
        field.makeMutable()
        field.set(default, hackedClassMap)
    }


    override fun createRegisteredListeners(listener: Listener, plugin: Plugin): MutableMap<Class<out Event>, MutableSet<RegisteredListener>> =
            default.createRegisteredListeners(listener
    , plugin)

    override fun getPluginFileFilters(): Array<Pattern> = default.pluginFileFilters

    override fun getPluginDescription(file: File) = default.getPluginDescription(file)

    override fun loadPlugin(file: File): Plugin {
        if (!file.exists()) {
            throw PluginFileMissingException(file)
        }
        return loadPlugin(file, getPluginDescription(file))
    }


     private fun loadPlugin(file: File, desc: PluginDescriptionFile) : Plugin{
        if (!file.exists()) {
            throw PluginFileMissingException(file)
        }
         val langScanner = LanguageScanner.createScanner(file, desc)
         if(!langScanner.isKotlinPlugin()) {
             return default.loadPlugin(file)
         }
        val parent = file.parentFile
        val dataFolder = File(parent, desc.name)
        server.unsafe.checkSupported(desc)
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
        plugin.logger.info("Disabling ${plugin.description.name}")

        val loader = plugin.javaClass.classLoader
        if(loader is KotlinPluginClassLoader) {
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
        if(loader !is KotlinPluginClassLoader) {
            throw PluginNotKotlinPluginException(plugin)
        }
        if(!loaders.contains(loader)) {
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

    fun getClass(name: String, skipHcm: Boolean) : Class<*>? {
        var cached = classes[name]
        if(!skipHcm) {
            if (cached == null) {
                cached = hackedClassMap.getSuper(name) // see if the javapluginloader has our class
            }
        }
        if(cached == null) {
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

    fun getClass(name: String)  = getClass(name, false)
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

}