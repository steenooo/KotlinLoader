package dev.steyn.kotlinloader.loader

import dev.steyn.kotlinloader.loader.reflect.HackedClassMap
import dev.steyn.kotlinloader.loader.reflect.reflect
import dev.steyn.kotlinloader.loader.reflect.reflectMutable
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginLoader
import org.bukkit.plugin.SimplePluginManager
import org.bukkit.plugin.java.JavaPluginLoader
import java.util.concurrent.ConcurrentMap
import java.util.regex.Pattern

object KotlinInjector {


    val fileAssociations by reflect<MutableMap<Pattern, PluginLoader>>(Bukkit.getPluginManager()) {
        SimplePluginManager::class.java.getDeclaredField("fileAssociations")
    }

    val loader: JavaPluginLoader by lazy<JavaPluginLoader> {
        for (loader in fileAssociations.values) {
            if (loader is JavaPluginLoader) {
                return@lazy loader
            }
        }
        throw Exception("Unable to find loader")
    }

    var javaMapField by reflectMutable<ConcurrentMap<String, Class<*>>>(loader) {
        JavaPluginLoader::class.java.getDeclaredField("classes")
    }

    lateinit var hackedMap: HackedClassMap

    lateinit var kotlinPluginLoader: KotlinPluginLoader
}
