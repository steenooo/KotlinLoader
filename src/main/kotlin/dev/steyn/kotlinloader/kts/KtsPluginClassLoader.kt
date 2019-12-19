package dev.steyn.kotlinloader.kts

import dev.steyn.kotlinloader.loader.AbstractPluginClassLoader
import dev.steyn.kotlinloader.loader.KotlinPluginLoader
import org.bukkit.Server
import java.io.File
import java.io.FileReader
import javax.script.ScriptEngineManager

class KtsPluginClassLoader(
        pluginLoader: KotlinPluginLoader,
        parent: ClassLoader,
        val file: File,
        val server: Server
) : AbstractPluginClassLoader(pluginLoader, emptyArray(), parent) {

    val engine = ScriptEngineManager(this).getEngineByExtension("kts")
    val builder = FileReader(file).use {
        println(engine)
        val x = engine.eval(it)
        println(x)
        x
    } as KtsPluginBuilder
    val description = KtsPluginDescription(builder)
    val folder = File(file.parent, builder.name)
    lateinit var plugin: KtsPlugin

    fun init() {
        plugin = KtsPlugin(builder.onEnable, builder.onDisable, builder.onLoad)
        plugin.init(file, folder, this, pluginLoader,  description, server)
    }


}