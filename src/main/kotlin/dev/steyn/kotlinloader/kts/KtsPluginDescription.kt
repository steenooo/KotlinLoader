package dev.steyn.kotlinloader.kts

import dev.steyn.kotlinloader.desc.KotlinPluginDescription
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.PluginAwareness
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginLoadOrder

class KtsPluginDescription(
        override val name: String,
        override val version: String,
        override val description: String?,
        override val website: String?,
        override val prefix: String?,
        override val apiVersion: String?,
        override val order: PluginLoadOrder = PluginLoadOrder.POSTWORLD,
        override val defaultPerm: PermissionDefault = PermissionDefault.OP,
        override val awareness: Set<PluginAwareness> = emptySet(),
        override val kotlinVersion: KotlinVersion = KotlinVersion.CURRENT
) : KotlinPluginDescription {


    constructor(builder: KtsPluginBuilder) : this(builder.name, builder.version, builder.description, builder.website, builder.prefix, builder.apiVersion, builder.order, builder.defaultPerm)

    override val main: String = "KtsPlugin"
    override val permissions: List<Permission>?
        get() = emptyList()
    override val lazyPermissions: Map<*, *>?
        get() = emptyMap<Any, Any>()


    override val bukkit: PluginDescriptionFile
        get() = PluginDescriptionFile(name, version, main)
}