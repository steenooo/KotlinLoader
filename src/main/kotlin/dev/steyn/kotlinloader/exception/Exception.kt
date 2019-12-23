package dev.steyn.kotlinloader.exception

import org.bukkit.plugin.InvalidDescriptionException
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
class PluginNotKotlinPluginException(plugin: Plugin) : KotlinPluginException("${plugin.name} is not a KotlinPlugin.")
class PluginFileMissingException(file: File) : KotlinPluginException("Unable to find ${file.path}")

//To make sure we don't terminate the loading process this inherits from InvalidDescriptionException.
class UnableToLoadScriptException(file: File): InvalidDescriptionException("Unable to execute script ${file.path}")
class InvalidPluginException : Exception {
    constructor() : super()

    constructor(msg: String) : super(msg)

    constructor(throwable: Throwable) : super(throwable)

    constructor(msg: String, throwable: Throwable) : super(msg, throwable)
}

fun notAvailableInScript(): Nothing =
    throw UnsupportedOperationException("This feature is not supported by Plugin Scripts.")


class ProtectedClassException(e: String) : ClassNotFoundException(e)