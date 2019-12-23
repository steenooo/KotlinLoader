package dev.steyn.kotlinloader.desc

import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.PluginAwareness
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginLoadOrder

interface KotlinPluginDescription {

    val main: String
    val name: String
    val version: String
    val description: String?
    val website: String?
    val prefix: String?
    val order: PluginLoadOrder
    val permissions: List<Permission>?
    val lazyPermissions: Map<*, *>?
    val defaultPerm: PermissionDefault
    val awareness: Set<PluginAwareness>
    val apiVersion: String?
    val kotlinVersion: KotlinVersion
    val authors: List<String>



    val bukkit: PluginDescriptionFile

}