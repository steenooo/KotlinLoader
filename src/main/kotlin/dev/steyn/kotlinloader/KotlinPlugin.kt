package dev.steyn.kotlinloader

import dev.steyn.kotlinloader.desc.KotlinPluginDescription
import dev.steyn.kotlinloader.exception.IllegalLoaderException
import dev.steyn.kotlinloader.jar.KotlinPluginClassLoader
import dev.steyn.kotlinloader.loader.AbstractPluginClassLoader
import dev.steyn.kotlinloader.loader.KotlinPluginLoader
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.PluginBase
import org.bukkit.plugin.PluginLogger
import java.io.*
import java.net.URL
import java.net.URLConnection
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KClass

open class KotlinPlugin : PluginBase() {


    companion object {
        @JvmStatic
        inline fun <reified T : KotlinPlugin> getPlugin(): T {
            return getPlugin(T::class)
        }

        @JvmStatic
        fun <T : KotlinPlugin> getPlugin(clazz: KClass<T>): T {
            return clazz.objectInstance ?: getPlugin(clazz.java)
        }

        @JvmStatic
        fun <T : KotlinPlugin> getPlugin(clazz: Class<T>) =
                (clazz.classLoader as KotlinPluginClassLoader).plugin as T
    }

    init {
        if (this::class.java.classLoader !is AbstractPluginClassLoader) {
            throw IllegalLoaderException()
        }
    }

    fun init(file: File, dataFolder: File, loader: AbstractPluginClassLoader, pluginLoader: KotlinPluginLoader, desc: KotlinPluginDescription, server: Server) {
        this._pluginDescriptionFile = desc
        this._file = file
        this._dataFolder = dataFolder
        this._loader = loader
        this._pluginLoader = pluginLoader
        this._server = server
        this._configFile = File(dataFolder, "config.yml")

        if (!isScript()) {
            this._config = reloadConfig0()
        }
    }

    private lateinit var _pluginDescriptionFile: KotlinPluginDescription
    private lateinit var _file: File
    private lateinit var _dataFolder: File
    private var _enabled: Boolean = false
    private var _naggable = false
    private lateinit var _loader: AbstractPluginClassLoader
    private lateinit var _pluginLoader: KotlinPluginLoader
    private lateinit var _server: Server
    private lateinit var _configFile: File
    private var _config: FileConfiguration? = null
    private val _logger: PluginLogger by lazy {
        PluginLogger(this)
    }

    internal var enabled: Boolean
        set(value) {
            if (_enabled != value) {
                _enabled = value
                if (_enabled) {
                    onEnable()
                } else {
                    onDisable()
                }
            }
        }
        get() = _enabled

    override fun getLogger(): Logger {
        return _logger
    }

    override fun onLoad() {}
    override fun onEnable() {}
    override fun onDisable() {}


    override fun isEnabled() = enabled

    override fun setNaggable(canNag: Boolean) {
        _naggable = canNag
    }


    override fun getDataFolder() = _dataFolder

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>) = false

    override fun getConfig() = _config!!
    override fun getPluginLoader() = _pluginLoader

    override fun getDescription() = _pluginDescriptionFile.bukkit


    override fun getServer() = _server

    override fun saveDefaultConfig() {
        if (!_configFile.exists()) {
            saveResource("config.yml", false)
        }
    }


    override fun reloadConfig() { //load from config file in plugin directory if present - otherwise load values from the default config (included in the jar)
            reloadConfig0()

    }

    private fun reloadConfig0(): FileConfiguration {
        _config = YamlConfiguration.loadConfiguration(_configFile)
        val defConfigStream = getResource("config.yml") ?: return config
        config.setDefaults(YamlConfiguration.loadConfiguration(InputStreamReader(defConfigStream, StandardCharsets.UTF_8)))
        return config
    }

    override fun saveConfig() {
        try {
            config.save(_configFile)
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Could not save config to $_configFile", e)
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String>? {
        return null
    }

    override fun isNaggable() = _naggable

    override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator? {
        return null
    }

    override fun getResource(filename: String): InputStream? {
        Objects.requireNonNull(filename, "Filename cannot be null")

        val url: URL = _loader.getResource(filename) ?: return null

        return try {
            val connection: URLConnection = url.openConnection()
            connection.useCaches = false
            connection.getInputStream()
        } catch (e: IOException) {
            null
        }
    }

    override fun saveResource(_resourcePath: String, replace: Boolean) {
        //blatantly copied from org.bukkit.plugin.java.JavaPlugin.java
        require(_resourcePath.isNotEmpty()) { "ResourcePath cannot be null or empty" }

        val resourcePath = _resourcePath.replace('\\', '/')
        val `in` = getResource(resourcePath)
                ?: throw IllegalArgumentException("The embedded resource '$resourcePath' cannot be found in $_file")

        val outFile = File(dataFolder, resourcePath)
        val lastIndex = resourcePath.lastIndexOf('/')
        val outDir = File(dataFolder, resourcePath.substring(0, if (lastIndex >= 0) lastIndex else 0))

        if (!outDir.exists()) {
            outDir.mkdirs()
        }

        try {
            if (!outFile.exists() || replace) {
                val out: OutputStream = FileOutputStream(outFile)
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
                out.close()
                `in`.close()
            } else {
                logger.log(Level.WARNING, "Could not save " + outFile.name + " to " + outFile + " because " + outFile.name + " already exists.")
            }
        } catch (ex: IOException) {
            logger.log(Level.SEVERE, "Could not save " + outFile.name + " to " + outFile, ex)
        }
    }

    open fun isScript() = false

}