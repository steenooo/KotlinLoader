package dev.steyn.kotlinloader.kts.command

import dev.steyn.kotlinloader.loader.reflect.reflectLazy
import org.bukkit.Bukkit
import org.bukkit.command.SimpleCommandMap

object Commands {
    val commandMap by reflectLazy<SimpleCommandMap>(Bukkit.getServer(), Bukkit.getServer().javaClass.getDeclaredField("commandMap"))
}
