package dev.steyn.kotlinloader.loader

import com.google.common.io.ByteStreams
import dev.steyn.kotlinloader.api.KotlinPlugin
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginLoader
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile

class CommonPluginLoader(
        val server: Server,
        val default: JavaPluginLoader
) : PluginLoader by default {

    val kotlin: KotlinPluginLoader
    val classes: ConcurrentHashMap<String, Class<*>>
    init {

        val field = JavaPluginLoader::class.java.getDeclaredField("classes")
        field.isAccessible = true
        this.classes = field.get(default) as ConcurrentHashMap<String, Class<*>>
        kotlin = KotlinPluginLoader(server, default, classes = classes)
    }

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
            (if(plugin is KotlinPlugin) kotlin else default).enablePlugin(plugin)


    override fun disablePlugin(plugin: Plugin) =
            (if(plugin is KotlinPlugin) kotlin else default).disablePlugin(plugin)


}