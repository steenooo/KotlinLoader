package dev.steyn.kotlinloader.loader.reflect

import dev.steyn.kotlinloader.loader.KotlinPluginLoader
import java.util.concurrent.ConcurrentMap

class HackedClassMap(val map: ConcurrentMap<String, Class<*>>, val kotlin: KotlinPluginLoader) : ConcurrentMap<String, Class<*>> by map {

    override fun get(key: String): Class<*>? = getSuper(key) ?: kotlin.getClass(key, true)

    fun getSuper(key: String) = map[key]
}