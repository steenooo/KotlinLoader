package dev.steyn.kotlinloader.api.event

import org.bukkit.Bukkit
import org.bukkit.event.Event

/**
 * The state of an Event if it was allowed.
 */
val <T : Event> T.isAllowed
get() = when(this) {
    is Cancellable -> !isCancelled
    else -> true
}

/**
 * Call an Event
 *
 * @return the called event
 */
fun <T : Event> callEvent(ev: T): T {
    Bukkit.getPluginManager().callEvent(ev)
    return ev
}

