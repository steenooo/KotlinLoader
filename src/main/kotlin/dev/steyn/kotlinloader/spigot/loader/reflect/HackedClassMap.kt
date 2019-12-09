package dev.steyn.kotlinloader.spigot.loader.reflect

import dev.steyn.kotlinloader.spigot.loader.KotlinPluginLoader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class HackedClassMap(val map: ConcurrentHashMap<String, Class<*>>, val kotlin: KotlinPluginLoader) : ConcurrentMap<String, Class<*>>by map {

    override fun get(key: String): Class<*>? =  getSuper(key) ?: kotlin.getClass(key, true)

    fun getSuper(key: String) = map[key]
}