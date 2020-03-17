package dev.steyn.kotlinloader.api.event

import dev.steyn.kotlinloader.api.KotlinPlugin
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.plugin.PluginManager

interface Cancellable : org.bukkit.event.Cancellable {

    companion object {

        @JvmSynthetic
        fun default(): Cancellable = object : Cancellable {

            private var cancel: Boolean = false
            override fun setCancelled(cancel: Boolean) {
                this.cancel = cancel
            }

            override fun isCancelled(): Boolean = cancel
        }

    }
}



