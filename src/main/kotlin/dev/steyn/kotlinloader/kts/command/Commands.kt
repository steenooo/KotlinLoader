package dev.steyn.kotlinloader.kts.command

import dev.steyn.kotlinloader.loader.reflect.reflectLazy
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.plugin.Plugin

object Commands {

    val commandMap by reflectLazy<SimpleCommandMap>(Bukkit.getServer(), Bukkit.getServer().javaClass.getDeclaredField("commandMap"))
    val constructor by lazy {
        val constructor = PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java)
        constructor.isAccessible = true
        constructor
    }


    fun register(pl: Plugin, label: String, name: String, exec: CommandExecutor)  : PluginCommand {
        val x = constructor.newInstance(name, pl) as PluginCommand
        x.setExecutor(exec)
        commandMap.register(label, x)
        return x
    }

}
