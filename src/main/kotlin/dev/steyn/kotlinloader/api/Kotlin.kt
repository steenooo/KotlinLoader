package dev.steyn.kotlinloader.api


/**
 * Indicates that this class should be treated as the Main class of a KotlinPlugin.
 * This annotation should be used when one would not use KotlinPlugin as the direct superclass.
 * Plugins should still extend KotlinPlugin somewhere in the hierarchy.
 * @see dev.steyn.kotlinloader.api.KotlinPlugin
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Kotlin