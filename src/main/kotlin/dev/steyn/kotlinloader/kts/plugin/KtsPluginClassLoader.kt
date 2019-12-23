package dev.steyn.kotlinloader.kts.plugin

import dev.steyn.kotlinloader.kts.ScriptExecutor
import dev.steyn.kotlinloader.loader.AbstractPluginClassLoader
import dev.steyn.kotlinloader.loader.KotlinPluginLoader
import org.bukkit.Server
import java.io.File
import java.io.FileReader

class KtsPluginClassLoader(
        pluginLoader: KotlinPluginLoader,
        parent: ClassLoader,
        val file: File,
        val server: Server
) : AbstractPluginClassLoader(pluginLoader, emptyArray(), parent) {


    lateinit var builder: KtsPluginBuilder
    val description: KtsPluginDescription
    val folder: File
    lateinit var plugin: KtsPlugin

    init {
            Thread {
                Thread.currentThread().contextClassLoader = this
                this.builder = FileReader(file).use {
                    ScriptExecutor<KtsPluginBuilder>(source = it).execute()
                }
            }.run {
                start()
                join()
            }
            description = KtsPluginDescription(builder)
            folder = File(file.parent, builder.name)

    }

    fun init() {
        plugin = KtsPlugin(builder.onEnable
                ?: {}, builder.onDisable ?: {}, builder.onLoad ?: {})
        plugin.init(file, folder, this, pluginLoader, description, server, false)
    }



}