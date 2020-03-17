package dev.steyn.kotlinloader.api.event

import org.bukkit.Bukkit
import org.bukkit.event.Event

val <T : Event> T.isAllowed
get() = when(this) {
    is Cancellable -> !isCancelled
    else -> true
}

fun <T : Event> callEvent(ev: T): T {
    Bukkit.getPluginManager().callEvent(ev)
    return ev
}

