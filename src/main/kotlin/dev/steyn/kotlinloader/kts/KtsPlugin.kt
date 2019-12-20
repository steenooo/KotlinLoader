package dev.steyn.kotlinloader.kts

import dev.steyn.kotlinloader.KotlinPlugin
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor

class KtsPlugin(
        val onEnable : PluginInitializer,
        val onDisable: PluginInitializer,
        val onLoad: PluginInitializer
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


    inline fun <reified E : Event> listen(priority: EventPriority = EventPriority.NORMAL, noinline handler: E.() -> Unit) {
        val executor = EventExecutor { _, event -> handler(event as E) }
        server.pluginManager.registerEvent(E::class.java, this, priority, executor, this)
    }
}