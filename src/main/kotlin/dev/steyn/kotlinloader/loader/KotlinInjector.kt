package dev.steyn.kotlinloader.loader

import dev.steyn.kotlinloader.loader.reflect.reflect
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginLoader
import org.bukkit.plugin.SimplePluginManager
import org.bukkit.plugin.java.JavaPluginLoader
import java.util.regex.Pattern

object KotlinInjector {


    val fileAssociations by reflect<MutableMap<Pattern, PluginLoader>>(Bukkit.getPluginManager()) {
        SimplePluginManager::class.java.getDeclaredField("fileAssociations")
    }

    val loader: JavaPluginLoader by lazy<JavaPluginLoader> {
        var _loader: JavaPluginLoader?
        for (loader in fileAssociations.values) {
            if (loader is JavaPluginLoader) {
                loader
            }
        }
        throw Exception("Unable to find loader")
    }


}
