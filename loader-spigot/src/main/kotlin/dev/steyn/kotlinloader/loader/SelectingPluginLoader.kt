package dev.steyn.kotlinloader.loader

import com.google.common.io.ByteStreams
import dev.steyn.kotlinloader.api.KotlinPlugin
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginLoader
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File
import java.util.jar.JarFile

class SelectingPluginLoader(
        val server: Server,
        val default: JavaPluginLoader,
        val kotlin: KotlinPluginLoader = KotlinPluginLoader(server, default)
) : PluginLoader by default {

    override fun loadPlugin(file: File): Plugin {
        val desc = getPluginDescription(file)

        val jar = JarFile(file)
        val entry = jar.getJarEntry(desc.main.replace('.', '/'))
        var bytes = jar.getInputStream(entry).use {
            ByteStreams.toByteArray(it)
        }

        return if(LanguageScanner(bytes).isKotlinPlugin()) kotlin.loadPlugin(file, desc) else default.loadPlugin(file)
    }

    override fun enablePlugin(plugin: Plugin) =
            (if(plugin is KotlinPlugin) kotlin else default).enablePlugin()


    override fun disablePlugin(plugin: Plugin) =
            (if(plugin is KotlinPlugin) kotlin else default).disablePlugin(plugin)


}