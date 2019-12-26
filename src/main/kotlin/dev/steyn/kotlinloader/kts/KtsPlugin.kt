package dev.steyn.kotlinloader.kts

import dev.steyn.kotlinloader.KotlinPlugin
import dev.steyn.kotlinloader.exception.notAvailableInScript
import dev.steyn.kotlinloader.kts.plugin.PluginInitializer
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.Listener
import java.io.InputStream

abstract class KtsPlugin internal constructor(
        internal val onEnable: PluginInitializer,
        internal val onDisable: PluginInitializer,
        internal val onLoad: PluginInitializer
) : KotlinPlugin(), Listener {

    override fun onEnable() {
        onEnable(this)
    }

    override fun onDisable() {
        onDisable(this)
    }

    override fun onLoad() {
        onLoad(this)
    }

    final override fun isScript() = true

    override fun getResource(filename: String): InputStream? {
        notAvailableInScript()
    }

    override fun saveResource(_resourcePath: String, replace: Boolean) {
        notAvailableInScript()
    }

    override fun saveDefaultConfig() {
        notAvailableInScript()
    }

    override fun getConfig(): FileConfiguration {
        notAvailableInScript()
    }

    override fun saveConfig() {
        notAvailableInScript()
    }


}
