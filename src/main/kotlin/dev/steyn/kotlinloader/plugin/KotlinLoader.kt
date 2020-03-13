package dev.steyn.kotlinloader.plugin

import dev.steyn.kotlinloader.bootstrap.KotlinLoaderPlugin
import dev.steyn.kotlinloader.loader.KotlinPluginLoader
import org.bukkit.Bukkit
import java.io.File
import kotlin.system.measureTimeMillis

class KotlinLoader(val plugin: KotlinLoaderPlugin) {


    fun load() {
        val manager = Bukkit.getPluginManager()
        manager.registerInterface(KotlinPluginLoader::class.java)
        val pluginsFolder = File(plugin.dataFolder.parent, "kotlin")
        if (!pluginsFolder.exists()) {
            pluginsFolder.mkdirs()
        }
       val time =  measureTimeMillis {
            manager.loadPlugins(pluginsFolder).forEach {
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
        }
        plugin.logger.info("Loaded Kotlin Plugins.. Took ${time}ms")
    }

}