package dev.steyn.kotlinloader.plugin

import dev.steyn.kotlinloader.bootstrap.KotlinLoaderPlugin
import dev.steyn.kotlinloader.loader.KotlinPluginLoader
import org.bukkit.Bukkit
import org.bukkit.plugin.SimplePluginManager
import java.io.File

class KotlinLoader(val plugin: KotlinLoaderPlugin) {


    fun load() {

        val manager = Bukkit.getPluginManager()
        if (manager !is SimplePluginManager) {
            throw Exception("Invalid PluginManager type")
        }
        manager.registerInterface(KotlinPluginLoader::class.java)
        val pluginsFolder = File(plugin.dataFolder.parent, "kotlin")
        if (!pluginsFolder.exists()) {
            pluginsFolder.mkdirs()
            pluginsFolder.mkdir()
        }
        manager.loadPlugins(pluginsFolder).toList().forEach {
            it.logger.info("Loading Kotlin Plugin ${it.description.fullName}..")
            try {
                it.onLoad()
            } catch (e: Throwable) {
                if (e is Error) {
                    throw e
                }
                e.printStackTrace()
            }
        }
        plugin.logger.info("Loaded Kotlin Plugins..")
    }

}