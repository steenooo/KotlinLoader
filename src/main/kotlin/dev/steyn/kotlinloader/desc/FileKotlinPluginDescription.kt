package dev.steyn.kotlinloader.desc

import dev.steyn.kotlinloader.loader.reflect.reflect
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.PluginAwareness
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginLoadOrder

class FileKotlinPluginDescription(val file: PluginDescriptionFile) : KotlinPluginDescription {

    override val main: String
        get() = file.main
    override val name: String
        get() = file.name
    override val version: String
        get() = file.version
    override val description: String?
        get() = file.description

    override val website: String?
        get() = file.website
    override val prefix: String?
        get() = file.prefix
    override val order: PluginLoadOrder
        get() = file.load
    override val permissions: List<Permission>?
        get() = file.permissions
    override val lazyPermissions: Map<*, *>? by reflect<Map<*, *>>(file) {
        file.javaClass.getDeclaredField("lazyPermissions")
    }
    override val defaultPerm: PermissionDefault
        get() = file.permissionDefault
    override val awareness: Set<PluginAwareness>
        get() = file.awareness
    override val apiVersion: String?
        get() = file.apiVersion

    override val kotlinVersion: KotlinVersion = KotlinVersion.CURRENT

    override val bukkit: PluginDescriptionFile
        get() = file
}