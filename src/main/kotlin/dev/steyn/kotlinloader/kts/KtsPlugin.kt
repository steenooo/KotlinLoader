package dev.steyn.kotlinloader.kts

import dev.steyn.kotlinloader.KotlinPlugin

class KtsPlugin(
        val onEnable : PluginInitializer,
        val onDisable: PluginInitializer,
        val onLoad: PluginInitializer
) : KotlinPlugin() {

    override fun onEnable() {
        onEnable(this)
    }

    override fun onDisable() {
        onDisable(this)
    }

    override fun onLoad() {
        onLoad(this)
    }
}