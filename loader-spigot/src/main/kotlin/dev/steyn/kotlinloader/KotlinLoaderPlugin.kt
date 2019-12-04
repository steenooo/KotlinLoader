package dev.steyn.kotlinloader

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class KotlinLoaderPlugin : JavaPlugin() {

    companion object {
        val instance
        get() = getPlugin(KotlinLoaderPlugin::class.java)
    }
}