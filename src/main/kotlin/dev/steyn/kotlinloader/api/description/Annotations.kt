package dev.steyn.kotlinloader.api.description


annotation class Main


@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class Authors

annotation class Author

annotation class PluginVersion()

annotation class ApiVersion()
annotation class KotlinVersion()
annotation class PluginWebsite()
annotation class PluginPrefix()
annotation class PluginName
