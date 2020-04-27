package dev.steyn.kotlinloader.api.event


/**
 * Lets the PluginLoader know it has to generate the proper methods/fields for Events to be executed.
 *
 * @see dev.steyn.kotlinloader.api.event.Event
 * @see org.bukkit.event.HandlerList
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class GenHandlers