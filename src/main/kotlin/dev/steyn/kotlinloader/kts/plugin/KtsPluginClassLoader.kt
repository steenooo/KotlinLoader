package dev.steyn.kotlinloader.kts.plugin

import dev.steyn.kotlinloader.bootstrap.KotlinLoaderPlugin
import dev.steyn.kotlinloader.kts.KtsPlugin
import dev.steyn.kotlinloader.kts.executeScript
import dev.steyn.kotlinloader.loader.AbstractPluginClassLoader
import dev.steyn.kotlinloader.loader.KotlinPluginLoader
import org.bukkit.Server
import java.io.File
import java.io.FileReader
import java.security.CodeSource

class KtsPluginClassLoader(
        pluginLoader: KotlinPluginLoader,
        parent: ClassLoader,
        val file: File,
        val server: Server
) : AbstractPluginClassLoader(pluginLoader, emptyArray(), parent) {


    companion object {
        val defaultImports = arrayOf(
                "dev.steyn.kotlinloader.kts.plugin",
                "dev.steyn.kotlinloader.kts.with",
                "dev.steyn.kotlinloader.kts.listen",
                "dev.steyn.kotlinloader.kts.command"
        )
    }


    private val builder: KtsPluginBuilder = FileReader(file).use {
        executeScript(it, this,
                *defaultImports)!!
    }
    val description: KtsPluginDescription
    val folder: File
    override lateinit var plugin: KtsPlugin

    init {
        description = KtsPluginDescription(builder)
            folder = File(file.parent, builder.name)

    }

    fun init() {

        val logger = KotlinLoaderPlugin.getInstance().logger

        val name = builder.getMainClass()
        logger.info("Generating KtsPlugin $name...")
        val data = ScriptPluginGenerator.generate(builder)
        val dot = name.lastIndexOf('.')
        if (dot != -1) {
            val pkgName = name.substring(0, dot)
            if (getPackage(pkgName) == null) {
                try {
                        definePackage(pkgName, null, null, null, null, null, null, null)
                } catch (ex: IllegalArgumentException) {
                    checkNotNull(getPackage(pkgName)) { "Cannot find package $pkgName" }
                }
            }
        }
        val source: CodeSource? = null
        val clz = defineClass(name, data, 0, data.size, source)
        val type = Class.forName("kotlin.jvm.functions.Function1")
        val constructor =  clz.getDeclaredConstructor(type, type, type)
        val emptyHandler: PluginInitializer = {}
        constructor.isAccessible = true
        this.plugin = constructor.newInstance(builder.onEnable ?: emptyHandler, builder.onDisable ?: emptyHandler, builder.onLoad ?: emptyHandler) as KtsPlugin
        plugin.init(file, folder, this, pluginLoader, description, server, false)
    }

}