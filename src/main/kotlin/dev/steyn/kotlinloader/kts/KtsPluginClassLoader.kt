package dev.steyn.kotlinloader.kts

import dev.steyn.kotlinloader.loader.AbstractPluginClassLoader
import dev.steyn.kotlinloader.loader.KotlinPluginLoader
import org.bukkit.Server
import java.io.File
import java.io.FileReader
import javax.script.Compilable
import javax.script.ScriptEngineManager

class KtsPluginClassLoader(
        pluginLoader: KotlinPluginLoader,
        parent: ClassLoader,
        val file: File,
        val server: Server
) : AbstractPluginClassLoader(pluginLoader, emptyArray(), parent) {

    lateinit var engine: Compilable
    lateinit var builder: KtsPluginBuilder
    val description: KtsPluginDescription
    val folder: File
    lateinit var plugin: KtsPlugin

    init {
            Thread {
                Thread.currentThread().contextClassLoader = this
                this.engine = ScriptEngineManager().getEngineByExtension("kts") as Compilable
                this.builder = FileReader(file).use {
                    val script = engine.compile(it)
                    script.eval()
                } as KtsPluginBuilder
            }.run {
                start()
                join()
            }
            description = KtsPluginDescription(builder)
            folder = File(file.parent, builder.name)

    }

    fun init() {
        plugin = KtsPlugin(builder.onEnable ?: {}, builder.onDisable ?: {}, builder.onLoad ?: {})
        plugin.init(file, folder, this, pluginLoader, description, server)
    }



}