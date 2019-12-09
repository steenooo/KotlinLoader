package dev.steyn.kotlinloader.spigot

import dev.steyn.kotlinloader.spigot.loader.KotlinPluginLoader
import org.bukkit.Bukkit
import org.bukkit.plugin.SimplePluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class KotlinLoader : JavaPlugin() {

    companion object {
        val instance
        get() = getPlugin(KotlinLoader::class.java)
    }


    override fun onLoad() {
        val manager = Bukkit.getPluginManager()
        if(manager !is SimplePluginManager) {
            throw Exception("Invalid PluginManager type")
        }
            manager.registerInterface(KotlinPluginLoader::class.java)
            val pluginsFolder = File(dataFolder.parent, "kotlin")
            if (!pluginsFolder.exists()) {
                pluginsFolder.mkdirs()
                pluginsFolder.mkdir()
            }
            manager.loadPlugins(pluginsFolder).toList().forEach {
                it.logger.info("Loading Kotlin Plugin ${it.description.fullName}..")
                try {
                    it.onLoad()
                } catch (e: Throwable) {
                    if(e is Error) {
                        throw e
                    }
                    e.printStackTrace()
                }
            }
        logger.info("Loaded Kotlin Plugins..")
    }
}