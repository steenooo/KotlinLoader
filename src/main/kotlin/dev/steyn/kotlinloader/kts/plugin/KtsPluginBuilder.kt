package dev.steyn.kotlinloader.kts.plugin

import dev.steyn.kotlinloader.kts.KtsPlugin
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.PluginAwareness
import org.bukkit.plugin.PluginLoadOrder


typealias PluginInitializer = KtsPlugin.() -> Unit
class KtsPluginBuilder {

    internal var onEnable: PluginInitializer? = null
    internal var onDisable: PluginInitializer? = null
    internal var onLoad: PluginInitializer? = null

    lateinit var mainClass: String
    lateinit var name: String
    lateinit var version: String
    var description: String? = null
    var website: String? = null
    var prefix: String? = null
    var apiVersion: String? = null
    var order: PluginLoadOrder = PluginLoadOrder.POSTWORLD
    var defaultPerm: PermissionDefault = PermissionDefault.OP
    var awareness: Set<PluginAwareness> = emptySet()
    var kotlinVersion: KotlinVersion = KotlinVersion.CURRENT
    var authors: List<String> = listOf()
    
    fun enable(initializer : PluginInitializer) {
        onEnable = initializer
    }

    fun disable(initializer : PluginInitializer) {
        onDisable = initializer
    }
    fun load(initializer : PluginInitializer) {
        onLoad = initializer
    }

    internal fun getMainClass() =  if(this::mainClass.isInitialized) mainClass else "kotlinloader.generated.$name"
}
