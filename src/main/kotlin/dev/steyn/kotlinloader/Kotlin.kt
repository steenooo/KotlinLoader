package dev.steyn.kotlinloader

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Kotlin(
        val ktsConfig: Boolean
)