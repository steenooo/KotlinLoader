package dev.steyn.kotlinloader.bungeecord

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.PluginManager
import java.io.File

class KotlinPluginManager : PluginManager(ProxyServer.getInstance()) {



    fun readFolder(file: File) {
        this.detectPlugins(file)
    }

    override fun loadPlugins() {
        super.loadPlugins()
    }
}