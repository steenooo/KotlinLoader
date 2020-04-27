package dev.steyn.kotlinloader.api.event

import dev.steyn.kotlinloader.event.ClassInstanceError
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Placeholder class.
 * This class should not to be used outside Plugins loaded by the KotlinLoader.
 *
 * No instance of this class may exist during runtime
 */
abstract class Event(async: Boolean) : Event(async) {

    companion object {
        init {
            throw ClassInstanceError("This class should not exist at runtime.")
        }
    }

    constructor() : this(false)

    override fun getHandlers(): HandlerList {
        throw ClassInstanceError("Instantiation of this class is illegal.")
    }

}
