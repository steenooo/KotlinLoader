package dev.steyn.kotlinloader.api

import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.PluginBase
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginLoader
import java.io.File
import java.io.InputStream
import java.util.logging.Logger

abstract class KotlinPlugin: PluginBase() {


    lateinit var source: File
    private var _enabled: Boolean = false

    val configFile by lazy {
        File(dataFolder, "config.yml")
    }
    private val _dataFolder by lazy {
        File(source, name)
    }


    var enabled
    set(value) {
        if(_enabled != value) {
            _enabled = value
            if(_enabled) {
                onEnable()
            } else {
                onDisable()
            }
        }
    }
    get() = _enabled

    override fun getDataFolder() = _dataFolder
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveDefaultConfig() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResource(filename: String): InputStream? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isNaggable(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLogger(): Logger {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun reloadConfig() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEnable() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoad() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setNaggable(canNag: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getConfig(): FileConfiguration {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPluginLoader(): PluginLoader {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getServer(): Server {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDisable() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveConfig() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveResource(resourcePath: String, replace: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}