package dev.steyn.kotlinloader.api

import dev.steyn.kotlinloader.loader.KotlinPluginClassLoader
import dev.steyn.kotlinloader.loader.KotlinPluginLoader
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.PluginBase
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginLogger
import java.io.*
import java.net.URL
import java.net.URLConnection
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger


class KotlinPlugin : PluginBase() {


    fun init(file: File, dataFolder: File, loader: KotlinPluginClassLoader, pluginLoader: KotlinPluginLoader, pluginDescriptionFile: PluginDescriptionFile, server: Server) {
        this._file = file
        this._dataFolder = dataFolder
        this._loader = loader
        this._pluginLoader = pluginLoader
        this._pluginDescriptionFile = pluginDescriptionFile
        this._server = server
    }

    private lateinit var _pluginDescriptionFile: PluginDescriptionFile
    private lateinit var _file: File
    private lateinit var _dataFolder: File
    private var _enabled: Boolean = false
    private var _naggable = false
    private lateinit var _loader: KotlinPluginClassLoader
    private lateinit var _pluginLoader: KotlinPluginLoader
    private lateinit var _server: Server

    val configFile by lazy {
        File(dataFolder, "config.yml")
    }
    var _config = reloadConfig0()

    val _logger = PluginLogger(this)

    var enabled
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

    override fun onLoad() {}
    override fun setNaggable(canNag: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEnable() {}
    override fun isEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDisable() {}

    override fun getDataFolder() = _dataFolder

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>) = false
    override fun getConfig() = _config
    override fun getPluginLoader() = _pluginLoader

    override fun getDescription() = _pluginDescriptionFile
    override fun getServer() = _server
    override fun saveDefaultConfig() {
        try {
            config.save(configFile)
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Could not save config to " + configFile, e)
        }
    }


    override fun reloadConfig() { //load from config file in plugin directory if present - otherwise load values from the default config (included in the jar)
        reloadConfig0()
    }

    private fun reloadConfig0(): FileConfiguration {
        _config = YamlConfiguration.loadConfiguration(configFile)
        val defConfigStream = getResource("config.yml") ?: return config
        config.setDefaults(YamlConfiguration.loadConfiguration(InputStreamReader(defConfigStream, StandardCharsets.UTF_8)))
        return config
    }

    override fun saveConfig() {
        try {
            getConfig().save(configFile)
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Could not save config to " + configFile, e)
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

    override fun getLogger(): Logger {
        return logger
    }

}