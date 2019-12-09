package dev.steyn.kotlinloader.loader

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginLoader
import java.io.File

class JvmPluginLoader(
        val parent: PluginLoader,
        val kotlin: KotlinPluginLoader
) : PluginLoader by parent {


    override fun loadPlugin(file: File): Plugin {
        val isKotlin = System.getProperties() as Boolean;
        if (isKotlin) {
            return parent.loadPlugin(file)
        }
        return kotlin.loadPlugin(file)


        TODO()

    }


}