package dev.steyn.kotlinloader.exception

import org.bukkit.plugin.Plugin
import java.io.File


open class KotlinPluginException : Exception {

    constructor() : super()

    constructor(msg: String) : super(msg)

    constructor(throwable: Throwable) : super(throwable)

    constructor(msg: String, throwable: Throwable) : super(msg, throwable)
}

class IllegalLoaderException : KotlinPluginException("ClassLoader must be a KotlinPluginClassLoader.")

class InjectException(msg: String) : KotlinPluginException(msg)
class PluginNotKotlinPluginException(plugin: Plugin) : KotlinPluginException("${plugin.name} is not a Kotlin Plugin.")
class PluginFileMissingException(file: File) : KotlinPluginException("Unable to find ${file.path}")

class InvalidPluginException : Exception {
    constructor() : super()

    constructor(msg: String) : super(msg)

    constructor(throwable: Throwable) : super(throwable)

    constructor(msg: String, throwable: Throwable) : super(msg, throwable)
}

class ProtectedClassException(e: String) : ClassNotFoundException(e)